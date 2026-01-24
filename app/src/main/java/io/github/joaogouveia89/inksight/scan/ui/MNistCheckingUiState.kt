package io.github.joaogouveia89.inksight.scan.ui

import androidx.camera.core.SurfaceRequest
import io.github.joaogouveia89.inksight.scan.domain.CharacterPrediction

data class MNistCheckingUiState(
    val surfaceRequest: SurfaceRequest? = null,
    val prediction: CharacterPrediction? = null,
    val loadingProgress: Float = 0f,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
