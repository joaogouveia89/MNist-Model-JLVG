package io.github.joaogouveia89.mnistmodelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.github.joaogouveia89.mnistmodelapp.ui.cameraPermission.CameraPermissionScreen
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

    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(
            viewModel = viewModel,
            modifier = modifier
        )
    } else {
        CameraPermissionScreen(
            modifier = modifier,
            cameraPermissionState = cameraPermissionState
        )
    }
}

@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    uiState.surfaceRequest?.let { request ->
        Box(modifier = Modifier.fillMaxSize()) {
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = modifier
            )
            CameraMaskOverlay(maskSize = viewModel.maskSize)

            uiState.prediction?.let { prediction ->
                Column(
                    modifier = modifier
                        .background(prediction.color)
                        .padding(12.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Row {
                        prediction.frame?.let {
                            Column(
                                Modifier.background(Color.White),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.padding(start = 8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Prediction: ${prediction.number}",
                            )
                            Row {
                                Text(
                                    text = "Confidence: ${prediction.confidence} %",
                                )
                                Icon(
                                    modifier = Modifier.padding(start = 8.dp),
                                    imageVector = prediction.icon,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun CameraMaskOverlay(maskSize: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val overlayColor = Color.Black.copy(alpha = 0.6f)

        val rectWidth = size.width * maskSize

        val left = (size.width - rectWidth) / 2f
        val top = (size.height - rectWidth) / 2f
        val right = left + rectWidth
        val bottom = top + rectWidth

        val holeRect = Rect(left, top, right, bottom)


        drawRect(
            color = overlayColor,
            size = size,
            blendMode = BlendMode.SrcOver
        )

        drawRect(
            color = Color.Transparent,
            topLeft = holeRect.topLeft,
            size = holeRect.size,
            blendMode = BlendMode.Clear
        )
    }
}