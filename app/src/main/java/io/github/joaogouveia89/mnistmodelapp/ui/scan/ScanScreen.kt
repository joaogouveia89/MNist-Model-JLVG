package io.github.joaogouveia89.mnistmodelapp.ui.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.joaogouveia89.mnistmodelapp.ui.cameraPermission.CameraPermissionScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        val job = viewModel.bindToCamera(lifecycleOwner)
        onDispose {
            job.cancel()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        ScanContainer(
            modifier = modifier,
            uiState = uiState,
            maskSize = viewModel.maskSize
        )
    } else {
        CameraPermissionScreen(
            modifier = modifier,
            cameraPermissionState = cameraPermissionState
        )
    }
}