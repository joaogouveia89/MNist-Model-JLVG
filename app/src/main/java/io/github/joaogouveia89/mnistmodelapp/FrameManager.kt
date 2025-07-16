package io.github.joaogouveia89.mnistmodelapp

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import io.github.joaogouveia89.mnistmodelapp.ktx.asByteArray
import io.github.joaogouveia89.mnistmodelapp.ktx.crop
import io.github.joaogouveia89.mnistmodelapp.ktx.rotateBitmap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import kotlin.math.pow

private const val TFLITE_MODEL_NAME = "mnist-jg.tflite"
private const val MODEL_IMAGE_WIDTH = 28
private const val MODEL_IMAGE_HEIGHT = 28

// in a more complex application I would use a dependency injection framework to avoid use the application context here
class FrameManager(
    val context: Context
) {
    val maskSize = 0.4f

    private val histMutex = Mutex()
    @Volatile
    private var previousHist: IntArray = intArrayOf()

    private var cropMeasurements: CropMeasurements = CropMeasurements()

    private val tfLiteOptions = Interpreter.Options()//can be configure to use GPUDelegate
    private val interpreter =
        Interpreter(FileUtil.loadMappedFile(context, TFLITE_MODEL_NAME), tfLiteOptions)

    suspend fun predictFrame(frame: Bitmap): PredictionResult? {
        val frameToAnalysis = frame.rotateBitmap(90f)
        if (cropMeasurements.isNotInitialized()) {
            val size = (frameToAnalysis.width * maskSize).toInt()
            cropMeasurements = CropMeasurements(
                size = size,
                left = (frameToAnalysis.width - size) / 2,
                top = (frameToAnalysis.height - size) / 2
            )
        }
        val cropped = frameToAnalysis.crop(
            cropMeasurements.size,
            cropMeasurements.top,
            cropMeasurements.left
        )
        val imageBytes = cropped.asByteArray()
        val histogram = generateHistogramFromData(imageBytes)
        val diff = histogramDifference(histogram)
        previousHist = histogram
        return diff.takeIf { it < 5000 }?.let {
            val input = preProcessCropped(cropped)
            evaluate(input)[0]
                .withIndex()
                .maxByOrNull { it.value }
                ?.let { prediction ->
                    PredictionResult(
                        predictedNumber = prediction.index,
                        confidence = prediction.value,
                        frame = cropped
                    )
                }
        }
    }

    private fun preProcessCropped(
        cropped: Bitmap
    ): Array<FloatArray> {
        val scaledImage = cropped
            .scale(28, 28, true)
        val imageBytes = scaledImage.asByteArray()
        val handleBytes = imageBytes.map { (it.toInt() and 0xFF) }
        val average = handleBytes.average()

        val input = Array(1) {
            FloatArray(MODEL_IMAGE_WIDTH * MODEL_IMAGE_HEIGHT) { i ->
                val candidate = handleBytes[i] / 255.0f
                if (handleBytes[i] < average) {
                    candidate
                } else 0f
            }
        }
        return input
    }

    private fun evaluate(input: Array<FloatArray>): Array<FloatArray> {
        // Prepare input in FLOAT32 with normalization

        // Model output: a vector of 10 classes (0-9)
        val output = Array(1) { FloatArray(10) }
        interpreter.run(input, output)
        return output
    }

    private fun generateHistogramFromData(data: ByteArray, bins: Int = 64): IntArray {
        val histogram = IntArray(bins)
        val binSize = 256 / bins

        for (value in data) {
            val luminance = value.toInt() and 0xFF
            val binIndex = luminance / binSize
            histogram[binIndex]++
        }

        return histogram
    }

    // Chi-squared difference
    private suspend fun histogramDifference(h1: IntArray): Double {
        return histMutex.withLock(owner = this) {
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
}

private data class CropMeasurements(
    val size: Int = 0,
    val top: Int = 0,
    val left: Int = 0,
) {
    fun isNotInitialized() = size == 0 && top == 0 && left == 0
}

data class PredictionResult(
    val predictedNumber: Int,
    val confidence: Float,
    val frame: Bitmap
)