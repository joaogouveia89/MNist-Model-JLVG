package io.github.joaogouveia89.mnistmodelapp

import androidx.camera.core.SurfaceRequest

data class MNistCheckingUiState(
    val surfaceRequest: SurfaceRequest? = null,
    val prediction: Pair<Int, Float>? = null,
)
