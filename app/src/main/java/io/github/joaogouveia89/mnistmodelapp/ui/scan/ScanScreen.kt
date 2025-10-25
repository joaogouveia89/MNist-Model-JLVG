package io.github.joaogouveia89.mnistmodelapp.ui.scan

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
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