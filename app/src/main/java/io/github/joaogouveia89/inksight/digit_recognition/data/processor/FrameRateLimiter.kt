package io.github.joaogouveia89.inksight.digit_recognition.data.processor

import io.github.joaogouveia89.inksight.digit_recognition.domain.FrameAnalysisConfig
import javax.inject.Inject

class FrameRateLimiter @Inject constructor() {
    private val minIntervalMs = 1000L / FrameAnalysisConfig.TARGET_FPS
    private var lastExecution = 0L

    fun canProcess(now: Long = System.currentTimeMillis()): Boolean {
        if (now - lastExecution < minIntervalMs) return false
        lastExecution = now
        return true
    }

    fun reset() {
        lastExecution = 0L
    }
}
