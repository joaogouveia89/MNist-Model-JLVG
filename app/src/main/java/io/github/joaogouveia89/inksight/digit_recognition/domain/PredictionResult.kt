package io.github.joaogouveia89.inksight.digit_recognition.domain

import android.graphics.Bitmap

data class PredictionResult(
    val predictedNumber: Int,
    val confidence: Float,
    val frame: Bitmap
)