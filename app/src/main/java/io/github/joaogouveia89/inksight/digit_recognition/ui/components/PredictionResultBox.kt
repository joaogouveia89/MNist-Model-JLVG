package io.github.joaogouveia89.inksight.digit_recognition.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import io.github.joaogouveia89.inksight.core.ui.theme.spacing
import io.github.joaogouveia89.inksight.helpers.BitmapUtils
import io.github.joaogouveia89.inksight.digit_recognition.domain.CharacterPrediction

@Composable
fun PredictionResultBox(
    modifier: Modifier = Modifier,
    prediction: CharacterPrediction?,
    loadingProgress: Float = 0f,
    isLoading: Boolean = false,
    borderColor: Color = Color.Green,
    onCorrect: () -> Unit = {},
    onIncorrect: () -> Unit = {},
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
            .padding(MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall) // 12.dp
    ) {
        Column {
            prediction?.frame?.let { frame ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PredictionFrame(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        frame = frame
                    )
                    PredictionResult(
                        modifier = Modifier.weight(1f),
                        prediction = prediction
                    )
                }
            }

            if (prediction != null && !isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall), // 12.dp
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = onIncorrect,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.4f),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Incorrect",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall)) // 12.dp
                    
                    FilledTonalIconButton(
                        onClick = onCorrect,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.7f),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Correct",
                            modifier = Modifier.size(MaterialTheme.spacing.large) // 24.dp
                        )
                    }
                }
            }
        }

        if (isLoading && prediction?.frame == null) {
            Text(
                text = stringResource(R.string.digit_recognition_stabilizing),
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
private fun PredictionResult(
    modifier: Modifier = Modifier,
    prediction: CharacterPrediction
) {
    Column(
        modifier = modifier.padding(start = MaterialTheme.spacing.small + MaterialTheme.spacing.extraSmall), // 12.dp
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.digit_recognition_prediction_result, prediction.number),
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.common_confidence, prediction.confidence),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.7f)
            )
            Icon(
                modifier = Modifier.padding(start = MaterialTheme.spacing.small).size(MaterialTheme.spacing.medium), // 8.dp / 16.dp
                imageVector = prediction.icon,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PredictionFrame(
    modifier: Modifier = Modifier,
    frame: Bitmap
) {
    Box(
        modifier
            .background(Color.White)
            .padding(MaterialTheme.spacing.extraSmall / 2) // 2.dp
    ) {
        Image(
            bitmap = frame.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(MaterialTheme.spacing.doubleExtraLarge) // 48.dp
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
