package io.github.joaogouveia89.inksight.digit_recognition.data.model

data class CropMeasurements(
    val size: Int = 0,
    val top: Int = 0,
    val left: Int = 0
) {
    fun isNotInitialized() = size == 0 && top == 0 && left == 0
}