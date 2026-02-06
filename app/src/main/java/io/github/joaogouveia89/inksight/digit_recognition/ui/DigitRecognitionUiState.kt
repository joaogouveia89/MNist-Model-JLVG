package io.github.joaogouveia89.inksight.digit_recognition.ui

import androidx.camera.core.SurfaceRequest
import io.github.joaogouveia89.inksight.digit_recognition.domain.CharacterPrediction

data class DigitRecognitionUiState(
    val surfaceRequest: SurfaceRequest? = null,
    val prediction: CharacterPrediction? = null,
    val loadingProgress: Float = 0f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
