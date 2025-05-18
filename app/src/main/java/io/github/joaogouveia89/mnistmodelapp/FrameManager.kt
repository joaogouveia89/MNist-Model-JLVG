package io.github.joaogouveia89.mnistmodelapp

import android.content.Context
import android.util.Size
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import kotlin.math.abs

private const val TFLITE_MODEL_NAME = "mnist-jg.tflite"
private const val MODEL_IMAGE_WIDTH = 28
private const val MODEL_IMAGE_HEIGHT = 28

// in a more complex application I would use a dependency injection framework to avoid use the application context here
class FrameManager(
    context: Context,
    frameResolution: Size?
) {
    val maskSize = 0.4f

    private val histMutex = Mutex()
    @Volatile private var previousHist: IntArray = intArrayOf()

    private val cropSize = frameResolution?.width?.let { (it * maskSize).toInt() } ?: 0
    private val cropLeft = frameResolution?.width?.let { (it - (cropSize)) / 2 } ?: 0
    private val cropTop = frameResolution?.height?.let { (it - (cropSize)) / 2 } ?: 0

    private val tfLiteOptions = Interpreter.Options()//can be configure to use GPUDelegate
    private val interpreter = Interpreter(FileUtil.loadMappedFile(context, TFLITE_MODEL_NAME), tfLiteOptions)

    suspend fun predictFrame(imageBuffer: ByteBuffer, rowStride: Int): Int?{
        val croppedData = ByteArray(cropSize * cropSize)

        for (y in 0 until cropSize) {
            imageBuffer.position((cropTop + y) * rowStride + cropLeft)
            imageBuffer.get(croppedData, y * cropSize, cropSize)
        }

        val preProcessedByteArray = ByteArray(croppedData.size) { i ->
            (croppedData[i].toInt() and 0xFF).toByte()
        }

        val histogram = generateHistogramFromData(croppedData)
        val diff = histogramDifference(histogram)
        previousHist = histogram
        if (diff < 5000){
            val redimenImage = redimen(28, cropSize, preProcessedByteArray)

            return evaluate(redimenImage)[0]
                .withIndex()
                .maxByOrNull { it.value }?.index
        }else{
            return null
        }
    }

    private fun redimen(goalDimen: Int, currentSize: Int, image: ByteArray): ByteArray {
        val resized = ByteArray(goalDimen * goalDimen)

        for (y in 0 until goalDimen) {
            val srcY = y * currentSize / goalDimen
            for (x in 0 until goalDimen) {
                val srcX = x * currentSize / goalDimen
                resized[y * goalDimen + x] = image[srcY * currentSize + srcX]
            }
        }

        return resized
    }

    private fun evaluate(imageBytes: ByteArray): Array<FloatArray>{
        // Prepare input in FLOAT32 with normalization
        val input = Array(1) {
            FloatArray(MODEL_IMAGE_WIDTH * MODEL_IMAGE_HEIGHT) { i ->
                (imageBytes[i].toInt() and 0xFF) / 255.0f
            }
        }

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

    private suspend fun histogramDifference(h1: IntArray): Int {
        return histMutex.withLock(owner = this) {
            val diff = h1.zip(previousHist) { a, b -> abs(a - b) }.sum()
            previousHist = h1
            diff
        }
    }
}