package io.github.joaogouveia89.mnistmodelapp.ui.scan

import android.app.Application
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
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
import io.github.joaogouveia89.mnistmodelapp.CharacterPrediction
import io.github.joaogouveia89.mnistmodelapp.FrameManager
import io.github.joaogouveia89.mnistmodelapp.MNistCheckingUiState
import io.github.joaogouveia89.mnistmodelapp.ktx.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class ScanViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MNistCheckingUiState())
    val uiState: StateFlow<MNistCheckingUiState> = _uiState.asStateFlow()

    // Executor for image analysis (dedicated single thread)
    private val executor = Executors.newSingleThreadExecutor()

    val cameraSelector: CameraSelector = DEFAULT_BACK_CAMERA

    private val frameManager = FrameManager(
        context = application.applicationContext
    )

    val maskSize: Float
        get() = frameManager.maskSize


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

    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        try {
            val image = imageProxy.image
            if (image == null) {
                imageProxy.close()
                return
            }

            val frame = image.toBitmap()

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    frameManager.predictFrame(frame)?.let { prediction ->
                        val confidencePercentage = (prediction.confidence * 100).toInt()

                        _uiState.update { currentState ->
                            currentState.copy(
                                prediction = CharacterPrediction(
                                    number = prediction.predictedNumber,
                                    confidence = confidencePercentage,
                                    frame = prediction.frame
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Log the error silently - we don't want to crash due to a single problematic frame
                    // In production, add appropriate logging (Firebase Crashlytics, etc.)
                }
            }
        } finally {

            imageProxy.close()
        }
    }

    override fun onCleared() {
        super.onCleared()
        frameManager.release()
        executor.shutdown()
    }
}