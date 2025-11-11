package io.github.joaogouveia89.mnistmodelapp.ui.scan

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.joaogouveia89.mnistmodelapp.MNistCheckingUiState
import io.github.joaogouveia89.mnistmodelapp.ui.cameraPermission.CameraPermissionScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (cameraPermissionState.status.isGranted) {
        ScanContent(
            modifier = modifier,
            uiState = uiState,
            maskSize = viewModel.maskSize,
            cameraPreviewUseCase = viewModel.cameraPreviewUseCase,
            imageAnalyzer = viewModel.imageAnalyzer,
            cameraSelector = viewModel.cameraSelector
        )
    } else {
        CameraPermissionScreen(
            modifier = modifier.fillMaxSize(),
            cameraPermissionState = cameraPermissionState
        )
    }
}

@Composable
private fun ScanContent(
    modifier: Modifier = Modifier,
    uiState: MNistCheckingUiState,
    maskSize: Float,
    cameraPreviewUseCase: Preview,
    imageAnalyzer: ImageAnalysis,
    cameraSelector: CameraSelector
) {
    CameraLifecycleManager(
        preview = cameraPreviewUseCase,
        imageAnalyzer = imageAnalyzer,
        cameraSelector = cameraSelector
    )

    ScanContainer(
        modifier = modifier,
        uiState = uiState,
        maskSize = maskSize
    )
}