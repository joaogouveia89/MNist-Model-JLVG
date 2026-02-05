package io.github.joaogouveia89.inksight.scan.ui.components

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.joaogouveia89.inksight.R
import io.github.joaogouveia89.inksight.scan.ui.MNistCheckingUiState

@Composable
fun ScanContainer(
    modifier: Modifier = Modifier,
    uiState: MNistCheckingUiState,
    maskSize: Float,
    onCorrect: () -> Unit = {},
    onIncorrect: () -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    val rectWidth = screenWidth * maskSize
    val topOffset = (screenHeight - rectWidth) / 2f

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

        // Guidance Text above the mask
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = (topOffset - 40.dp).coerceAtLeast(16.dp))
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.scan_position_guidance),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        if (uiState.prediction != null || uiState.isLoading) {
            PredictionResultBox(
                modifier = Modifier.align(Alignment.BottomCenter),
                prediction = uiState.prediction,
                isLoading = uiState.isLoading,
                loadingProgress = uiState.loadingProgress,
                onCorrect = onCorrect,
                onIncorrect = onIncorrect
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
