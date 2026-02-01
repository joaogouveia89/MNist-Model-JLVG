package io.github.joaogouveia89.inksight.scan.ui

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.joaogouveia89.inksight.core.ui.components.CameraPermissionScreen
import io.github.joaogouveia89.inksight.scan.ui.components.CameraLifecycleManager
import io.github.joaogouveia89.inksight.scan.ui.components.ScanContainer

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe error messages and show Snackbar
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            viewModel.execute(ScanCommand.DismissError)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (cameraPermissionState.status.isGranted) {
            ScanContent(
                uiState = uiState,
                maskSize = viewModel.maskSize,
                cameraPreviewUseCase = viewModel.cameraPreviewUseCase,
                imageAnalyzer = viewModel.imageAnalyzer,
                cameraSelector = viewModel.cameraSelector,
                onCameraError = { error -> viewModel.execute(ScanCommand.OnCameraError(error)) },
                onCorrect = { viewModel.execute(ScanCommand.OnPredictionCorrect) },
                onIncorrect = { viewModel.execute(ScanCommand.OnPredictionIncorrect) }
            )
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
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun ScanContent(
    uiState: MNistCheckingUiState,
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

    ScanContainer(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        maskSize = maskSize,
        onCorrect = onCorrect,
        onIncorrect = onIncorrect
    )
}
