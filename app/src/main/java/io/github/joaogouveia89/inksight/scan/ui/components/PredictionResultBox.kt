package io.github.joaogouveia89.inksight.scan.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joaogouveia89.inksight.R
import io.github.joaogouveia89.inksight.core.ui.theme.MNistModelAppTheme
import io.github.joaogouveia89.inksight.helpers.BitmapUtils
import io.github.joaogouveia89.inksight.scan.domain.CharacterPrediction

@Composable
fun PredictionResultBox(
    modifier: Modifier = Modifier,
    prediction: CharacterPrediction?,
    loadingProgress: Float = 0f,
    isLoading: Boolean = false,
    borderColor: Color = Color.Green,
) {
    Box(
        modifier = modifier
            .background(prediction?.color ?: Color.White)
            .drawWithContent {
                drawContent()
                if (isLoading) {
                    drawProgressiveBorder(
                        progress = loadingProgress,
                        color = borderColor,
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
            .padding(12.dp)
    ) {
        prediction?.frame?.let { frame ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                PredictionFrame(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    frame = frame
                )
                PredictionResult(prediction)
            }
        }

        if (isLoading && prediction?.frame == null) {
            Text(
                text = stringResource(R.string.predictionBoxStabilizing),
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
private fun PredictionResult(prediction: CharacterPrediction) {
    Column(
        modifier = Modifier.padding(start = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.predictionBoxResult, prediction.number),
        )
        Row {
            Text(
                text = stringResource(R.string.predictionBoxConfidence, prediction.confidence),
            )
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = prediction.icon,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun PredictionFrame(
    modifier: Modifier = Modifier,
    frame: Bitmap
) {
    Column(
        modifier.background(Color.White),
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            bitmap = frame.asImageBitmap(),
            contentDescription = null,
        )
    }
}

fun DrawScope.drawProgressiveBorder(
    progress: Float,
    color: Color,
    strokeWidth: Float
) {
    val width = size.width
    val height = size.height
    val perimeter = 2 * (width + height)
    val progressLength = perimeter * progress

    val path = Path()
    var currentLength = 0f

    // Top edge
    if (progressLength > currentLength) {
        val topLength = minOf(width, progressLength - currentLength)
        path.moveTo(0f, 0f)
        path.lineTo(topLength, 0f)
        currentLength += width
    }

    // Right edge
    if (progressLength > currentLength) {
        val rightLength = minOf(height, progressLength - currentLength)
        if (path.isEmpty) path.moveTo(width, 0f)
        else path.lineTo(width, 0f)
        path.lineTo(width, rightLength)
        currentLength += height
    }

    // Bottom edge
    if (progressLength > currentLength) {
        val bottomLength = minOf(width, progressLength - currentLength)
        if (path.isEmpty) path.moveTo(width, height)
        else path.lineTo(width, height)
        path.lineTo(width - bottomLength, height)
        currentLength += width
    }

    // Left edge
    if (progressLength > currentLength) {
        val leftLength = minOf(height, progressLength - currentLength)
        if (path.isEmpty) path.moveTo(0f, height)
        else path.lineTo(0f, height)
        path.lineTo(0f, height - leftLength)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

@Preview(showBackground = true, name = "Idle / Loading 0%")
@Composable
private fun PredictionResultBoxIdlePreview() {
    MNistModelAppTheme {
        PredictionResultBox(
            prediction = null,
            isLoading = true,
            loadingProgress = 0f
        )
    }
}

@Preview(showBackground = true, name = "Loading 50%")
@Composable
private fun PredictionResultBoxLoadingPreview() {
    MNistModelAppTheme {
        PredictionResultBox(
            prediction = null,
            isLoading = true,
            loadingProgress = 0.5f
        )
    }
}

@Preview(showBackground = true, name = "Prediction High Confidence")
@Composable
private fun PredictionResultBoxHighConfidencePreview() {
    MNistModelAppTheme {
        PredictionResultBox(
            prediction = CharacterPrediction(
                number = 7,
                confidence = 95,
                frame = BitmapUtils.createMockBitmap("7")
            ),
            isLoading = false
        )
    }
}

@Preview(showBackground = true, name = "Medium Confidence")
@Composable
private fun PredictionResultBoxMediumConfidencePreview() {
    MNistModelAppTheme {
        PredictionResultBox(
            prediction = CharacterPrediction(
                number = 3,
                confidence = 72,
                frame = BitmapUtils.createMockBitmap("3")
            )
        )
    }
}

@Preview(showBackground = true, name = "Low Confidence")
@Composable
private fun PredictionResultBoxLowConfidencePreview() {
    MNistModelAppTheme {
        PredictionResultBox(
            prediction = CharacterPrediction(
                number = 5,
                confidence = 45,
                frame = BitmapUtils.createMockBitmap("5")
            )
        )
    }
}

@Preview(showBackground = true, name = "Very Low Confidence")
@Composable
private fun PredictionResultBoxVeryLowConfidencePreview() {
    MNistModelAppTheme {
        PredictionResultBox(
            prediction = CharacterPrediction(
                number = 2,
                confidence = 28,
                frame = BitmapUtils.createMockBitmap("2")
            )
        )
    }
}

@Preview(showBackground = true, name = "No Frame")
@Composable
private fun PredictionResultBoxNoFramePreview() {
    MNistModelAppTheme {
        PredictionResultBox(
            prediction = CharacterPrediction(
                number = 9,
                confidence = 88,
                frame = null
            )
        )
    }
}



