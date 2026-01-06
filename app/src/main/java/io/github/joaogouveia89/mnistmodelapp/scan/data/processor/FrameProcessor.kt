package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import android.graphics.Bitmap
import io.github.joaogouveia89.mnistmodelapp.scan.domain.FrameAnalysisConfig
import io.github.joaogouveia89.mnistmodelapp.scan.domain.FrameProcessorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Coordinates camera frame processing
 * Orchestrates ImagePreprocessor, HistogramAnalyzer, and MnistModel
 */

class FrameProcessor(
    private val frameRateLimiter: FrameRateLimiter,
    private val framePipeline: FramePipeline,
    private val frameGate: FrameGate,
    private val inferenceRunner: InferenceRunner,
) {

    private val _state = MutableStateFlow<FrameProcessorState>(FrameProcessorState.Idle)
    val state: StateFlow<FrameProcessorState> = _state.asStateFlow()

    private var stableStartTime: Long? = null

    suspend fun process(frame: Bitmap) {
        if (!frameRateLimiter.canProcess()) return

        val processed = framePipeline.process(frame)

        val isStable = frameGate.shouldProcess(processed.bytes)

        val now = System.currentTimeMillis()

        if (isStable) {
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
                }
                stableStartTime = null // reset after prediction
            } else {
                _state.emit(FrameProcessorState.Loading(progress))
            }

        } else {
            // Unstable, resets counter and returns to Idle.
            stableStartTime = null
            _state.emit(FrameProcessorState.Idle)
        }
    }

    fun reset() {
        frameRateLimiter.reset()
        framePipeline.reset()
        frameGate.reset()
        stableStartTime = null
        _state.value = FrameProcessorState.Idle
    }

    fun release() {
        inferenceRunner.close()
    }
}
