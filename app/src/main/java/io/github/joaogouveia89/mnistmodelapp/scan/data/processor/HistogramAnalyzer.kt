package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.pow

/**
 * Histogram analysis to detect significant changes between frames.
 */
class HistogramAnalyzer(
    private val differenceThreshold: Double = 5000.0
) {
    private val histMutex = Mutex()

    @Volatile
    private var previousHist: IntArray = intArrayOf()

    fun generateHistogram(data: ByteArray, bins: Int = 64): IntArray {
        val histogram = IntArray(bins)
        val binSize = 256 / bins

        for (value in data) {
            val luminance = value.toInt() and 0xFF
            val binIndex = luminance / binSize
            histogram[binIndex]++
        }

        return histogram
    }

    suspend fun isSignificantChange(currentHistogram: IntArray): Boolean {
        val difference = calculateDifference(currentHistogram)
        return difference < differenceThreshold
    }

    private suspend fun calculateDifference(h1: IntArray): Double {
        return histMutex.withLock {
            if (previousHist.isEmpty()) {
                previousHist = h1
                return@withLock Double.MAX_VALUE
            }

            val epsilon = 1e-10
            val diff = h1.zip(previousHist) { a, b ->
                val numerator = (a - b).toDouble().pow(2)
                val denominator = a + b + epsilon
                numerator / denominator
            }.sum()

            previousHist = h1
            diff
        }
    }

    fun reset() {
        previousHist = intArrayOf()
    }
}