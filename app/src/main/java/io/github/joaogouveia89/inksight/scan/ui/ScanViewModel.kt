package io.github.joaogouveia89.inksight.scan.ui

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
import io.github.joaogouveia89.inksight.scan.data.processor.FrameProcessor
import io.github.joaogouveia89.inksight.scan.domain.CharacterPrediction
import io.github.joaogouveia89.inksight.scan.domain.FrameAnalysisConfig
import io.github.joaogouveia89.inksight.scan.domain.FrameProcessorState
import io.github.joaogouveia89.inksight.scan.domain.repository.InferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * Commands represent actions that the View can trigger.
 */
sealed interface ScanCommand {
    data class OnCameraError(val message: String) : ScanCommand
    object DismissError : ScanCommand
    object OnPredictionCorrect : ScanCommand
    object OnPredictionIncorrect : ScanCommand
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val frameProcessor: FrameProcessor,
    private val inferenceRepository: InferenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MNistCheckingUiState())
    val uiState: StateFlow<MNistCheckingUiState> = _uiState.asStateFlow()

    private val executor = Executors.newSingleThreadExecutor()

    // Constants and UseCases are maintained as properties for the camera setup.
    val cameraSelector: CameraSelector = DEFAULT_BACK_CAMERA
    val maskSize: Float = FrameAnalysisConfig.MASK_SIZE

    val cameraPreviewUseCase: Preview = Preview.Builder()
        .build()
        .apply {
            setSurfaceProvider { newSurfaceRequest ->
                _uiState.update { it.copy(surfaceRequest = newSurfaceRequest) }
            }
        }

    val imageAnalyzer: ImageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            setAnalyzer(executor, ::analyzeImage)
        }

    init {
        observeProcessorState()
    }

    /**
     * Single entry point for View interactions.
     */
    fun execute(command: ScanCommand) {
        when (command) {
            is ScanCommand.OnCameraError -> handleCameraError(command.message)
            ScanCommand.DismissError -> dismissError()
            ScanCommand.OnPredictionCorrect -> handlePredictionFeedback(true)
            ScanCommand.OnPredictionIncorrect -> handlePredictionFeedback(false)
        }
    }

    private fun handlePredictionFeedback(isCorrect: Boolean) {
        val currentPrediction = _uiState.value.prediction
        if (currentPrediction != null) {
            viewModelScope.launch {
                inferenceRepository.saveInference(currentPrediction, isCorrect)
                // Opcional: Dar um feedback visual ou resetar a predição atual
            }
        }
    }

    private fun observeProcessorState() {
        viewModelScope.launch {
            frameProcessor.state.collect { state ->
                updateUiStateFromProcessor(state)
            }
        }
    }

    private fun updateUiStateFromProcessor(state: FrameProcessorState) {
        _uiState.update { currentState ->
            when (state) {
                is FrameProcessorState.Idle -> {
                    currentState.copy(prediction = null, loadingProgress = 0f, isLoading = false)
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

    private fun handleCameraError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }

    private fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImage(imageProxy: ImageProxy) {
        try {
            val image = imageProxy.image ?: return
            val plane = image.planes[0]
            val buffer = plane.buffer
            val yData = ByteArray(buffer.remaining())
            buffer.get(yData)

            val width = image.width
            val height = image.height
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride

            viewModelScope.launch(Dispatchers.Default) {
                val frame = imageToBitmap(yData, rowStride, pixelStride, width, height)
                try {
                    frameProcessor.process(frame)
                } catch (e: Exception) {
                    execute(ScanCommand.OnCameraError("Processing error: ${e.localizedMessage}"))
                }
            }
        } finally {
            imageProxy.close()
        }
    }

    private fun imageToBitmap(
        yData: ByteArray,
        rowStride: Int,
        pixelStride: Int,
        width: Int,
        height: Int
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

    override fun onCleared() {
        super.onCleared()
        frameProcessor.release()
        executor.shutdown()
    }
}
