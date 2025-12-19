package io.github.joaogouveia89.mnistmodelapp.scan.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.core.graphics.createBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.joaogouveia89.mnistmodelapp.scan.data.ml.MnistModel
import io.github.joaogouveia89.mnistmodelapp.scan.data.processor.FrameGate
import io.github.joaogouveia89.mnistmodelapp.scan.data.processor.FramePipeline
import io.github.joaogouveia89.mnistmodelapp.scan.data.processor.FrameProcessor
import io.github.joaogouveia89.mnistmodelapp.scan.data.processor.FrameRateLimiter
import io.github.joaogouveia89.mnistmodelapp.scan.data.processor.HistogramAnalyzer
import io.github.joaogouveia89.mnistmodelapp.scan.data.processor.ImagePreprocessor
import io.github.joaogouveia89.mnistmodelapp.scan.data.processor.InferenceRunner
import io.github.joaogouveia89.mnistmodelapp.scan.domain.CharacterPrediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.Executors

private const val STABILITY_WINDOW_SIZE = 10 // Last 10 frames
private const val STABILITY_THRESHOLD = 0.02 // 2% maximum variation between histograms

private const val MASK_SIZE = 0.4f

class ScanViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MNistCheckingUiState())
    val uiState: StateFlow<MNistCheckingUiState> = _uiState.asStateFlow()

    // Executor for image analysis (dedicated single thread)
    private val executor = Executors.newSingleThreadExecutor()

    val cameraSelector: CameraSelector = DEFAULT_BACK_CAMERA

    private val frameProcessor = FrameProcessor(
        frameRateLimiter = FrameRateLimiter,
        framePipeline = FramePipeline(
            imagePreprocessor = ImagePreprocessor(),
            maskSize = MASK_SIZE
        ),
        frameGate = FrameGate(
            histogramAnalyzer = HistogramAnalyzer(
                differenceThreshold = 5000.0,
                stabilityWindowSize = STABILITY_WINDOW_SIZE,
                stabilityThreshold = STABILITY_THRESHOLD
            )
        ),
        inferenceRunner = InferenceRunner(
            imagePreprocessor = ImagePreprocessor(),
            model = MnistModel(application.applicationContext)
        )
    )

    val maskSize: Float
        get() = MASK_SIZE


    val cameraPreviewUseCase: Preview = Preview.Builder()
        .build()
        .apply {
            setSurfaceProvider { newSurfaceRequest ->
                _uiState.update { currentState ->
                    currentState.copy(surfaceRequest = newSurfaceRequest)
                }
            }
        }

    val imageAnalyzer: ImageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(executor, ::analyzeImage)
        }


    init {
        viewModelScope.launch {
            frameProcessor.predictions
                .filterNotNull()
                .collect { prediction ->
                    _uiState.update { currentState ->
                        val confidencePercentage = (prediction.confidence * 100).toInt()
                        currentState.copy(
                            prediction = CharacterPrediction(
                                number = prediction.predictedNumber,
                                confidence = confidencePercentage,
                                frame = prediction.frame
                            )
                        )
                    }
                }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        try {
            val image = imageProxy.image
            if (image == null) {
                imageProxy.close()
                return
            }

            val (yData, rowStride, pixelStride) = image.planes[0].let {
                val bufferSize = it.buffer.remaining()
                val yData = ByteArray(bufferSize)
                it.buffer.get(yData)

                Triple(yData, it.rowStride, it.pixelStride)
            }

            val width = image.width
            val height = image.height

            viewModelScope.launch(Dispatchers.Default) {
                val frame = imageToBitmap(
                    yData = yData,
                    rowStride = rowStride,
                    pixelStride = pixelStride,
                    width = width,
                    height = height
                )
                try {
                    frameProcessor.process(frame)
                } catch (e: Exception) {
                    // Log the error silently
                }
            }
        } finally {
            imageProxy.close()
        }
    }

    override fun onCleared() {
        super.onCleared()
        frameProcessor.release()
        executor.shutdown()
    }

    private fun imageToBitmap(
        yData: ByteArray,
        rowStride: Int,
        pixelStride: Int,
        width: Int,
        height: Int,
    ): Bitmap {
        val bitmap = createBitmap(width, height, Bitmap.Config.ALPHA_8)

        if (pixelStride == 1 && rowStride == width) {
            bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(yData))
            return bitmap
        }

        val dest = ByteArray(width * height)
        var pos = 0
        for (row in 0 until height) {
            val rowStart = row * rowStride
            for (col in 0 until width) {
                dest[pos++] = yData[rowStart + col * pixelStride]
            }
        }

        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(dest))
        return bitmap
    }
}