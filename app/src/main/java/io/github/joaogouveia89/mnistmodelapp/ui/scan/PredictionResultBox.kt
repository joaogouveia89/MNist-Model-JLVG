package io.github.joaogouveia89.mnistmodelapp.ui.scan

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.joaogouveia89.mnistmodelapp.CharacterPrediction
import io.github.joaogouveia89.mnistmodelapp.R
import io.github.joaogouveia89.mnistmodelapp.helpers.BitmapUtils
import io.github.joaogouveia89.mnistmodelapp.ui.theme.MNistModelAppTheme

@Composable
fun PredictionResultBox(
    modifier: Modifier = Modifier,
    prediction: CharacterPrediction
) {
    prediction.frame?.let { frame ->
        Column(
            modifier = modifier
                .background(prediction.color)
                .padding(12.dp)
        ) {
            Row {
                PredictionFrame(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    frame = frame
                )
                PredictionResult(prediction)
            }
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

@Preview(showBackground = true, name = "High Confidence")
@Composable
private fun PredictionResultBoxHighConfidencePreview() {
    MNistModelAppTheme {
        PredictionResultBox(
            prediction = CharacterPrediction(
                number = 7,
                confidence = 95,
                frame = BitmapUtils.createMockBitmap("7")
            )
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



