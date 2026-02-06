package io.github.joaogouveia89.inksight.digit_recognition.domain

import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.joaogouveia89.inksight.core.ui.theme.Orange
import io.github.joaogouveia89.inksight.core.ui.theme.RedAlert
import io.github.joaogouveia89.inksight.core.ui.theme.SuccessGreen
import io.github.joaogouveia89.inksight.core.ui.theme.Yellow

data class CharacterPrediction(
    val number: Int,
    val confidence: Int,
    val frame: Bitmap?
) {
    val color: Color
        get() = when (confidence) {
            in 0..50 -> RedAlert
            in 50..69 -> Orange
            in 70..89 -> Yellow
            in 90..100 -> SuccessGreen
            else -> Color.Green
        }
    val icon: ImageVector
        get() = when (confidence) {
            in 0..30 -> Icons.Outlined.ReportProblem
            in 30..50 -> Icons.AutoMirrored.Outlined.HelpOutline
            in 50..70 -> Icons.AutoMirrored.Outlined.Help
            else -> Icons.Outlined.CheckCircleOutline
        }
}
