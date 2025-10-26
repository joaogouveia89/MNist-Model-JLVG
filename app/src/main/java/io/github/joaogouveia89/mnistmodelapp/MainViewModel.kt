package io.github.joaogouveia89.mnistmodelapp

import android.app.Application
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import io.github.joaogouveia89.mnistmodelapp.ktx.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

private const val TARGET_FPS: Int = 5 // 5 FPS

class MainViewModel(private val application: Application) : AndroidViewModel(application) {
    // Used to set up a link between the Camera and your UI.
    val uiState: StateFlow<MNistCheckingUiState>
        get() = _uiState

    private val _uiState = MutableStateFlow(MNistCheckingUiState())
    private val predictionInterval: Long = 1000L / TARGET_FPS

    private var lastMeasureTime = 0L

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

    private val frameManager = FrameManager(
        context = application.applicationContext
    )

    val maskSize: Float
        get() = frameManager.maskSize


    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if(currentTime - lastMeasureTime >= predictionInterval){
            lastMeasureTime = currentTime
            val image = imageProxy.image ?: return
            val frame = image.toBitmap()
            viewModelScope.launch(Dispatchers.IO) {
                frameManager.predictFrame(frame)?.also { prediction ->
                    val confidence = (prediction.confidence * 100).toInt()
                    _uiState.update {
                        it.copy(prediction = CharacterPrediction(
                            number = prediction.predictedNumber,
                            confidence = confidence,
                            frame = prediction.frame
                        ))
                    }
                }

            }
        }
        imageProxy.close()
    }

    fun bindToCamera(lifecycleOwner: LifecycleOwner)=
        viewModelScope.launch {
            val processCameraProvider = ProcessCameraProvider.awaitInstance(application.applicationContext)

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