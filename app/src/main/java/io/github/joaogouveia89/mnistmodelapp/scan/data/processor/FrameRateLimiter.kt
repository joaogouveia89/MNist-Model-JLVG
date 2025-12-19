package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

private const val TARGET_FPS = 5

object FrameRateLimiter {
    private const val MIN_INTERVAL_MS = 1000L / TARGET_FPS
    private var lastExecution = 0L

    fun canProcess(now: Long = System.currentTimeMillis()): Boolean {
        if (now - lastExecution < MIN_INTERVAL_MS) return false
        lastExecution = now
        return true
    }

    fun reset() {
        lastExecution = 0L
    }
}
