package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

class FrameGate(
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
