package io.github.joaogouveia89.mnistmodelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.joaogouveia89.mnistmodelapp.ui.cameraPermission.CameraPermissionScreen
import io.github.joaogouveia89.mnistmodelapp.ui.scan.ScanScreen
import io.github.joaogouveia89.mnistmodelapp.ui.theme.MNistModelAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels {
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
        enableEdgeToEdge()
        setContent {
            MNistModelAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
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
        ScanScreen(
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