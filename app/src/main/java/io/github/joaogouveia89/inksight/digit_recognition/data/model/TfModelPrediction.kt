package io.github.joaogouveia89.inksight.digit_recognition.data.model

data class TfModelPrediction(
    val predictedClass: Int,
    val confidence: Float
)