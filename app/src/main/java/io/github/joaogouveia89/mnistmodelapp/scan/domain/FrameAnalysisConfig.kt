package io.github.joaogouveia89.mnistmodelapp.scan.domain

object FrameAnalysisConfig {
    const val STABILITY_WINDOW_SIZE = 10 // Last 10 frames used for stability analysis
    const val STABILITY_THRESHOLD = 0.02 // Max allowed histogram variation (2%)
    const val MASK_SIZE = 0.4f // Relative crop size used for frame analysis
    const val TARGET_FPS = 5 // Maximum number of frames processed per second
}