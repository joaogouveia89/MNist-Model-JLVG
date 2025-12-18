package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.abs
import kotlin.math.pow

/**
 * Histogram analysis to detect significant changes between frames.
 */
class HistogramAnalyzer(
    private val differenceThreshold: Double = 5000.0,
    private val stabilityWindowSize: Int = 10,
    private val stabilityThreshold: Double = 0.02
) {
    private val histMutex = Mutex()

    @Volatile
    private var previousHist: IntArray = intArrayOf()

    // Circular buffer for checking stability.
    private val histogramBuffer = ArrayDeque<IntArray>(stabilityWindowSize)

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

    /**
     * Add histogram to stability buffer
     */
    fun addToStabilityBuffer(histogram: IntArray) {
        if (histogramBuffer.size >= stabilityWindowSize) {
            histogramBuffer.removeFirst()
        }
        histogramBuffer.addLast(histogram)
    }

    /**
     * Check if the last N frames are stable (little variation between histograms).
     */
    fun isStable(): Boolean {
        if (histogramBuffer.size < stabilityWindowSize) {
            return false // We still don't have enough frames.
        }

        // Compares each pair of consecutive histograms
        for (i in 0 until histogramBuffer.size - 1) {
            val diff = calculateHistogramDifference(
                histogramBuffer[i],
                histogramBuffer[i + 1]
            )

            if (diff > stabilityThreshold) {
                return false // Significant variation was found.
            }
        }

        return true // All frames are stable.
    }

    /**
     * Calculates the normalized difference between two histograms
     * Returns a value between 0.0 (identical) and 1.0 (completely different)
     */
    private fun calculateHistogramDifference(hist1: IntArray, hist2: IntArray): Double {
        require(hist1.size == hist2.size) { "Histograms must have same size" }

        var totalDiff = 0L
        var totalCount = 0L

        for (i in hist1.indices) {
            totalDiff += abs(hist1[i] - hist2[i])
            totalCount += hist1[i] + hist2[i]
        }

        return if (totalCount > 0) {
            totalDiff.toDouble() / totalCount.toDouble()
        } else {
            0.0
        }
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
        histogramBuffer.clear()
    }
}