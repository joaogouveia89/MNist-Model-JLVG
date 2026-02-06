package io.github.joaogouveia89.inksight.history.domain.model

import android.graphics.Bitmap

data class HistoryItem(
    val predictedNumber: Int,
    val confidence: Int,
    val image: Bitmap?,
    val isCorrect: Boolean,
    val timestamp: Long
)
