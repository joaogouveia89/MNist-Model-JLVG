package io.github.joaogouveia89.inksight.scan.data.processor

import javax.inject.Inject

class FrameGate @Inject constructor(
    private val histogramAnalyzer: HistogramAnalyzer
) {

    suspend fun shouldProcess(bytes: ByteArray): Boolean {
        val histogram = histogramAnalyzer.generateHistogram(bytes)

        histogramAnalyzer.addToStabilityBuffer(histogram)

        if (!histogramAnalyzer.isSignificantChange(histogram)) {
            return false
        }

        return histogramAnalyzer.isStable()
    }

    fun reset() {
        histogramAnalyzer.reset()
    }
}
