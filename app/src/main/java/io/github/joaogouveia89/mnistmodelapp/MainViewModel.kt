package io.github.joaogouveia89.mnistmodelapp

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.math.abs

class MainViewModel : ViewModel() {
    // Used to set up a link between the Camera and your UI.
    val uiState: StateFlow<MNistCheckingUiState>
        get() = _uiState

    val maskSize = 0.4f

    private val _uiState = MutableStateFlow(MNistCheckingUiState())
    private var previousHist: IntArray = intArrayOf()

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _uiState.update {
                it.copy(surfaceRequest = newSurfaceRequest)
            }
        }
    }
    private val executor = Executors.newSingleThreadExecutor()

    private val imageAnalyzer = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply { setAnalyzer(executor, ::analyzeImage) }

    private fun generateHistogramFromData(data: ByteArray, bins: Int = 64): IntArray {
        val histogram = IntArray(bins)
        val binSize = 256 / bins

        for (value in data) {
            val luminance = value.toInt() and 0xFF
            val binIndex = luminance / binSize
            histogram[binIndex]++
        }

        return histogram
    }


    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return

        val width = image.width
        val height = image.height

        val cropSize = (width * maskSize).toInt()
        val cropLeft = (width - cropSize) / 2
        val cropTop = (height - cropSize) / 2

        val yBuffer = image.planes[0].buffer
        val yRowStride = image.planes[0].rowStride
        val croppedData = ByteArray(cropSize * cropSize)

        for (y in 0 until cropSize) {
            yBuffer.position((cropTop + y) * yRowStride + cropLeft)
            yBuffer.get(croppedData, y * cropSize, cropSize)
        }

        imageProxy.close()

        viewModelScope.launch(Dispatchers.IO) {
            val histogram = generateHistogramFromData(croppedData)
            val diff = histogramDifference(histogram, previousHist)
            previousHist = histogram
            _uiState.update {
                if (diff < 10000){
                    println(croppedData.map { b -> b.toInt() and 0xFF }.joinToString())
                    it.copy(prediction =  5)
                }else{
                    it.copy(prediction =  null)
                }

            }
        }
    }

    private fun histogramDifference(h1: IntArray, h2: IntArray): Int =
        h1.zip(h2) { a, b -> abs(a - b) }.sum()


    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)

        processCameraProvider.bindToLifecycle(
            lifecycleOwner, DEFAULT_BACK_CAMERA, cameraPreviewUseCase
        )

        processCameraProvider.unbindAll()
        processCameraProvider.bindToLifecycle(
            lifecycleOwner,
            DEFAULT_BACK_CAMERA,
            cameraPreviewUseCase,
            imageAnalyzer
        )
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }
    }
}