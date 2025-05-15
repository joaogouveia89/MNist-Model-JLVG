package io.github.joaogouveia89.mnistmodelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import io.github.joaogouveia89.mnistmodelapp.ui.theme.MNistModelAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionScreen(
    cameraPermissionState: PermissionState,
    modifier: Modifier = Modifier
) {
    val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
        "Para que o app funcione corretamente, precisamos acessar a sua câmera.\n\n" +
                "Ela será usada para capturar imagens com números escritos à mão e reconhecer os caracteres usando nosso modelo de inteligência artificial."
    } else {
        "Olá! 👋\n\nEste app utiliza a câmera para identificar números escritos à mão com ajuda de um modelo de IA treinado por nós.\n\n" +
                "Para começar, permita o acesso à câmera."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Ícone de câmera",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = textToShow,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { cameraPermissionState.launchPermissionRequest() },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Permitir acesso à câmera")
        }
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
        Box(modifier = Modifier.fillMaxSize()){
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = modifier
            )
            CameraMaskOverlay(maskSize = viewModel.maskSize)
            uiState.prediction?.let { prediction ->
                Text(
                    modifier = modifier
                        .background(Color.White)
                        .padding(12.dp)
                        .align(Alignment.BottomCenter),
                    text = "Prediction: $prediction",
                )
            }
        }
    }
}

@Composable
fun CameraMaskOverlay(maskSize: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val overlayColor = Color.Black.copy(alpha = 0.6f)

        val rectWidth = size.width * maskSize
        //val rectHeight = size.height * 0.2f
        val rectHeight = rectWidth

        val left = (size.width - rectWidth) / 2f
        val top = (size.height - rectHeight) / 2f
        val right = left + rectWidth
        val bottom = top + rectHeight

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