package io.github.joaogouveia89.inksight.digit_recognition.domain

sealed interface FrameProcessorState {
    object Idle : FrameProcessorState
    data class Loading(val progress: Float) : FrameProcessorState // 0..1
    data class Prediction(val result: PredictionResult) : FrameProcessorState
}
