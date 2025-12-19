package io.github.joaogouveia89.mnistmodelapp.scan.ui.components

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.joaogouveia89.mnistmodelapp.scan.ui.MNistCheckingUiState

@Composable
fun ScanContainer(
    modifier: Modifier = Modifier,
    uiState: MNistCheckingUiState,
    maskSize: Float
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (val surfaceRequest = uiState.surfaceRequest) {
            null -> {
                CameraLoadingState()
            }

            else -> {
                CameraXViewfinder(
                    surfaceRequest = surfaceRequest,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        CameraMaskOverlay(
            maskSize = maskSize
        )

        if (uiState.prediction != null || uiState.isLoading) {
            PredictionResultBox(
                modifier = Modifier.align(Alignment.BottomCenter),
                prediction = uiState.prediction,
                isLoading = uiState.isLoading,
                loadingProgress = uiState.loadingProgress
            )
        }
    }
}

@Composable
private fun CameraLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}