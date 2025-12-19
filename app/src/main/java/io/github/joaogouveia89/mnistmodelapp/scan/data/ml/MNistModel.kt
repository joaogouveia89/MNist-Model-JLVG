package io.github.joaogouveia89.mnistmodelapp.scan.data.ml

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Load and run the TensorFlow Lite model.
 */
class MnistModel(
    private val context: Context,
    private val modelName: String = "mnist-jg.tflite"
) {
    private val tfLiteOptions = Interpreter.Options() // It can be configured to use GPUDelegate.

    private val interpreter: Interpreter by lazy {
        Interpreter(loadModelFile(), tfLiteOptions)
    }

    fun predict(input: Array<FloatArray>): ModelPrediction? {
        val output = Array(1) { FloatArray(10) }
        interpreter.run(input, output)

        return output[0]
            .withIndex()
            .maxByOrNull { it.value }
            ?.let { prediction ->
                ModelPrediction(
                    predictedClass = prediction.index,
                    confidence = prediction.value
                )
            }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun close() {
        interpreter.close()
    }
}

data class ModelPrediction(
    val predictedClass: Int,
    val confidence: Float
)