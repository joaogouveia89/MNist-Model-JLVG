package io.github.joaogouveia89.inksight.digit_recognition.ui.components

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import io.github.joaogouveia89.inksight.R
import io.github.joaogouveia89.inksight.core.ui.theme.spacing
import io.github.joaogouveia89.inksight.digit_recognition.ui.DigitRecognitionUiState

@Composable
fun DigitRecognitionContainer(
    modifier: Modifier = Modifier,
    uiState: DigitRecognitionUiState,
    maskSize: Float,
    onCorrect: () -> Unit = {},
    onIncorrect: () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Camera Preview (Background)
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

        // Mask Overlay
        CameraMaskOverlay(
            maskSize = maskSize
        )

        // Guidance Layout: Mirrors the mask logic using weights and aspect ratio
        Column(modifier = Modifier.fillMaxSize()) {
            // Top section: Takes exactly half of the remaining vertical space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = stringResource(R.string.digit_recognition_position_guidance),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.large)
                        .padding(bottom = MaterialTheme.spacing.medium) // Fixed gap between text and mask
                )
            }

            // Middle section: Represents the mask hole area
            // aspectRatio(width/height). Since height = width * maskSize, ratio = 1/maskSize
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / maskSize)
            )

            // Bottom section: Takes the other half of the vertical space
            Spacer(modifier = Modifier.weight(1f))
        }

        // Prediction Result Box
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
