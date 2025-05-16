package io.github.joaogouveia89.mnistmodelapp

import android.content.Context
import android.util.Size
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
    private var previousHist: IntArray = intArrayOf()

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
        println("diff = $diff")
        previousHist = histogram
        if (diff < 1000){
            println(preProcessedByteArray.joinToString())
            return 5
        }else{
            return null
        }
    }

    private fun evaluate(imageBytes: ByteArray): Array<Array<FloatArray>>{
        val outputArr = Array(1) {
            Array(MODEL_IMAGE_WIDTH) {
                FloatArray(MODEL_IMAGE_HEIGHT)
            }
        }
        interpreter.run(imageBytes, outputArr)
        return outputArr
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

    private fun histogramDifference(h1: IntArray): Int =
        h1.zip(previousHist) { a, b -> abs(a - b) }.sum()
}