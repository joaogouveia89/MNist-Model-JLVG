package io.github.joaogouveia89.mnistmodelapp.scan.domain

object FrameAnalysisConfig {
    /**
     * Number of consecutive frames required for stability analysis.
     * At 5 FPS, 10 frames = 2 seconds of relatively stable camera positioning.
     */
    const val STABILITY_WINDOW_SIZE = 10

    /**
     * Maximum allowed normalized histogram variation between consecutive frames (5%).
     * Lower values require more camera stability before prediction.
     */
    const val STABILITY_THRESHOLD = 0.05

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
     * Lower values = more sensitive to movement = stricter stability requirement.
     * Typical range: 2000-5000 depending on lighting and camera conditions.
     */
    const val DIFFERENCE_THRESHOLD = 8000.0

    /**
     * Time (in milliseconds) the camera must remain stable before triggering prediction.
     * Provides visual feedback through loading progress indicator.
     * Balances between quick responsiveness and ensuring frame stability.
     */
    const val STABILITY_DURATION_MS = 1500L  // 1.5 seconds

    /**
     * Time (in milliseconds) to display prediction result before allowing new capture.
     * Gives user time to see and understand the prediction.
     */
    const val PREDICTION_DISPLAY_DURATION_MS = 3000L // 3 seconds
}