package io.github.joaogouveia89.mnistmodelapp.scan.data.model

data class TfModelPrediction(
    val predictedClass: Int,
    val confidence: Float
)