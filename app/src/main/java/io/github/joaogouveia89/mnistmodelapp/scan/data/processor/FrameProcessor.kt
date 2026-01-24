package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import android.graphics.Bitmap
import io.github.joaogouveia89.mnistmodelapp.scan.domain.FrameAnalysisConfig
import io.github.joaogouveia89.mnistmodelapp.scan.domain.FrameProcessorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Coordinates camera frame processing
 * Orchestrates ImagePreprocessor, HistogramAnalyzer, and MnistModel
 */

class FrameProcessor @Inject constructor(
    private val frameRateLimiter: FrameRateLimiter,
    private val framePipeline: FramePipeline,
    private val frameGate: FrameGate,
    private val inferenceRunner: InferenceRunner,
) {

    private val _state = MutableStateFlow<FrameProcessorState>(FrameProcessorState.Idle)
    val state: StateFlow<FrameProcessorState> = _state.asStateFlow()

    private var stableStartTime: Long? = null
    private var predictionShownAt: Long? = null
    private var hasPredictedForCurrentStability = false

    suspend fun process(frame: Bitmap) {
        if (!frameRateLimiter.canProcess()) return

        val processed = framePipeline.process(frame)

        val isStable = frameGate.shouldProcess(processed.bytes)

        val now = System.currentTimeMillis()

        if (isStable) {
            if (hasPredictedForCurrentStability) {
                // Already made a prediction for this stable session.
                // Keep the current prediction on screen.
                return
            }

            // If we are still in the display duration of a previous prediction session,
            // wait before starting a new stability timer or showing loading progress.
            predictionShownAt?.let { shownAt ->
                if (now - shownAt < FrameAnalysisConfig.PREDICTION_DISPLAY_DURATION_MS) {
                    return
                }
            }

            if (stableStartTime == null) {
                stableStartTime = now
            }

            val elapsed = now - stableStartTime!!
            val progress = (elapsed.toFloat() / FrameAnalysisConfig.STABILITY_DURATION_MS).coerceIn(0f, 1f)

            if (elapsed >= FrameAnalysisConfig.STABILITY_DURATION_MS) {
                // Stability time reached, run the model
                val result = inferenceRunner.run(processed.bitmap)
                if (result != null) {
                    _state.emit(FrameProcessorState.Prediction(result))
                    predictionShownAt = now
                    hasPredictedForCurrentStability = true
                }
                stableStartTime = null // reset after prediction
            } else {
                _state.emit(FrameProcessorState.Loading(progress))
            }

        } else {
            // Unstable, resets stability tracking
            stableStartTime = null
            hasPredictedForCurrentStability = false

            // Clear prediction state only if display duration has passed
            val shownAt = predictionShownAt
            if (shownAt == null || now - shownAt >= FrameAnalysisConfig.PREDICTION_DISPLAY_DURATION_MS) {
                predictionShownAt = null
                _state.emit(FrameProcessorState.Idle)
            }
        }
    }

    fun reset() {
        frameRateLimiter.reset()
        framePipeline.reset()
        frameGate.reset()
        stableStartTime = null
        predictionShownAt = null
        hasPredictedForCurrentStability = false
        _state.value = FrameProcessorState.Idle
    }

    fun release() {
        inferenceRunner.close()
    }
}
