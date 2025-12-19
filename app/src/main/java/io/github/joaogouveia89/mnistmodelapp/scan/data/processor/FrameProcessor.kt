package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import android.graphics.Bitmap
import io.github.joaogouveia89.mnistmodelapp.scan.domain.PredictionResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Coordinates camera frame processing
 * Orchestrates ImagePreprocessor, HistogramAnalyzer, and MnistModel
 */

class FrameProcessor(
    private val frameRateLimiter: FrameRateLimiter,
    private val framePipeline: FramePipeline,
    private val frameGate: FrameGate,
    private val inferenceRunner: InferenceRunner
) {

    private val _predictions = MutableSharedFlow<PredictionResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val predictions = _predictions.asSharedFlow()

    suspend fun process(frame: Bitmap) {
        if (!frameRateLimiter.canProcess()) return

        val processed = framePipeline.process(frame)

        if (!frameGate.shouldProcess(processed.bytes)) return

        val result = inferenceRunner.run(processed.bitmap) ?: return

        _predictions.emit(result)
    }

    fun reset() {
        frameRateLimiter.reset()
        framePipeline.reset()
        frameGate.reset()
    }

    fun release() {
        inferenceRunner.close()
    }
}