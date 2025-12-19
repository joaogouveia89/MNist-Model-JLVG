package io.github.joaogouveia89.mnistmodelapp.scan.data.ml

import android.content.Context
import io.github.joaogouveia89.mnistmodelapp.scan.data.model.TfModelPrediction
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

    fun predict(input: Array<FloatArray>): TfModelPrediction? {
        val output = Array(1) { FloatArray(10) }
        interpreter.run(input, output)

        return output[0]
            .withIndex()
            .maxByOrNull { it.value }
            ?.let { prediction ->
                TfModelPrediction(
                    predictedClass = prediction.index,
                    confidence = prediction.value
                )
            }
    }

    private fun loadModelFile(): MappedByteBuffer =
        context.assets.openFd(modelName).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                inputStream.channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    fileDescriptor.startOffset,
                    fileDescriptor.declaredLength
                )
            }
        }

    fun close() {
        interpreter.close()
    }
}