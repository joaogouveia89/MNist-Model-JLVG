package io.github.joaogouveia89.mnistmodelapp.ui.scan

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.joaogouveia89.mnistmodelapp.MNistCheckingUiState

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    uiState: MNistCheckingUiState,
    maskSize: Float
) {
    uiState.surfaceRequest?.let { request ->
        Box(modifier = Modifier.fillMaxSize()) {
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = modifier
            )
            CameraMaskOverlay(maskSize = maskSize)

            uiState.prediction?.let { prediction ->
                PredictionResultBox(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    prediction = prediction
                )
            }
        }
    }
}