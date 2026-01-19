package io.github.joaogouveia89.mnistmodelapp.scan.ui

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.joaogouveia89.mnistmodelapp.scan.data.processor.FrameProcessor
import io.github.joaogouveia89.mnistmodelapp.scan.domain.CharacterPrediction
import io.github.joaogouveia89.mnistmodelapp.scan.domain.FrameAnalysisConfig
import io.github.joaogouveia89.mnistmodelapp.scan.domain.FrameProcessorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val frameProcessor: FrameProcessor
) : ViewModel() {

    private val _uiState = MutableStateFlow(MNistCheckingUiState())
    val uiState: StateFlow<MNistCheckingUiState> = _uiState.asStateFlow()

    // Executor for image analysis (dedicated single thread)
    private val executor = Executors.newSingleThreadExecutor()

    val cameraSelector: CameraSelector = DEFAULT_BACK_CAMERA

    val maskSize: Float = FrameAnalysisConfig.MASK_SIZE

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
            frameProcessor.state.collect { state ->
                _uiState.update { currentState ->
                    when (state) {
                        is FrameProcessorState.Idle -> {
                            currentState.copy(
                                prediction = null,
                                loadingProgress = 0f,
                                isLoading = false
                            )
                        }

                        is FrameProcessorState.Loading -> {
                            currentState.copy(
                                prediction = null,
                                loadingProgress = state.progress,
                                isLoading = true
                            )
                        }

                        is FrameProcessorState.Prediction -> {
                            val confidencePercentage = (state.result.confidence * 100).toInt()
                            currentState.copy(
                                prediction = CharacterPrediction(
                                    number = state.result.predictedNumber,
                                    confidence = confidencePercentage,
                                    frame = state.result.frame
                                ),
                                loadingProgress = 1f,
                                isLoading = false
                            )
                        }
                    }
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
                    _uiState.update { it.copy(errorMessage = "Processing error: ${e.localizedMessage}") }
                }
            }
        } finally {
            imageProxy.close()
        }
    }

    fun onCameraError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    fun onErrorMessageDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
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
