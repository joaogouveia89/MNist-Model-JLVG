package io.github.joaogouveia89.inksight.digit_recognition.ui

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.joaogouveia89.inksight.core.ui.components.CameraPermissionScreen
import io.github.joaogouveia89.inksight.core.ui.theme.spacing
import io.github.joaogouveia89.inksight.digit_recognition.ui.components.CameraLifecycleManager
import io.github.joaogouveia89.inksight.digit_recognition.ui.components.DigitRecognitionContainer

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DigitRecognitionScreen(
    modifier: Modifier = Modifier,
    viewModel: DigitRecognitionViewModel,
    onNavigateToHistory: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe error messages and show Snackbar
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.execute(DigitRecognitionCommand.DismissError)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            DigitRecognitionContent(
                uiState = uiState,
                maskSize = viewModel.maskSize,
                cameraPreviewUseCase = viewModel.cameraPreviewUseCase,
                imageAnalyzer = viewModel.imageAnalyzer,
                cameraSelector = viewModel.cameraSelector,
                onCameraError = { error -> viewModel.execute(DigitRecognitionCommand.OnCameraError(error)) },
                onCorrect = { viewModel.execute(DigitRecognitionCommand.OnPredictionCorrect) },
                onIncorrect = { viewModel.execute(DigitRecognitionCommand.OnPredictionIncorrect) }
            )

            // History Navigation Button
            IconButton(
                onClick = onNavigateToHistory,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = MaterialTheme.spacing.doubleExtraLarge, end = MaterialTheme.spacing.medium) // Adjusted for status bar/safe area
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = "History",
                    tint = Color.White
                )
            }
        } else {
            CameraPermissionScreen(
                modifier = Modifier.fillMaxSize(),
                cameraPermissionState = cameraPermissionState
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = MaterialTheme.spacing.medium)
        )
    }
}

@Composable
private fun DigitRecognitionContent(
    uiState: DigitRecognitionUiState,
    maskSize: Float,
    cameraPreviewUseCase: Preview,
    imageAnalyzer: ImageAnalysis,
    cameraSelector: CameraSelector,
    onCameraError: (String) -> Unit,
    onCorrect: () -> Unit,
    onIncorrect: () -> Unit
) {
    CameraLifecycleManager(
        preview = cameraPreviewUseCase,
        imageAnalyzer = imageAnalyzer,
        cameraSelector = cameraSelector,
        onError = onCameraError
    )

    DigitRecognitionContainer(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        maskSize = maskSize,
        onCorrect = onCorrect,
        onIncorrect = onIncorrect
    )
}
