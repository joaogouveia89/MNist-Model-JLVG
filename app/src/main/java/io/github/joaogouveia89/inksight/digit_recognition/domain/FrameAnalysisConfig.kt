package io.github.joaogouveia89.inksight.digit_recognition.domain

object FrameAnalysisConfig {
    /**
     * Number of consecutive frames required for stability analysis.
     * Reducing this makes the initial stability check faster.
     */
    const val STABILITY_WINDOW_SIZE = 5

    /**
     * Maximum allowed normalized histogram variation between consecutive frames.
     * Increased from 0.05 to 0.10 to be less sensitive to minor camera shakes.
     */
    const val STABILITY_THRESHOLD = 0.10

    /**
     * Relative size of the center crop area (40% of frame dimensions).
     * Focuses analysis on the central region where digits are expected.
     */
    const val MASK_SIZE = 0.4f

    /**
     * Maximum number of frames processed per second.
     * Balances responsiveness with computational cost.
     */
    const val TARGET_FPS = 5

    /**
     * Threshold for normalized histogram distance metric: Σ[(a-b)²/(a+b+ε)].
     * Values BELOW this threshold indicate stable frame (minimal scene change).
     */
    const val DIFFERENCE_THRESHOLD = 8000.0

    /**
     * Time (in milliseconds) the camera must remain stable before triggering prediction.
     * Reduced from 1500ms to 800ms for faster response.
     */
    const val STABILITY_DURATION_MS = 800L

    /**
     * Time (in milliseconds) to display prediction result before allowing new capture.
     * Gives user time to see and understand the prediction.
     */
    const val PREDICTION_DISPLAY_DURATION_MS = 3000L // 3 seconds
}