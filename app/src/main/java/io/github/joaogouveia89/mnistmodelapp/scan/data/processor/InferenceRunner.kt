package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import android.graphics.Bitmap
import io.github.joaogouveia89.mnistmodelapp.scan.data.ml.MnistModel
import io.github.joaogouveia89.mnistmodelapp.scan.domain.PredictionResult

class InferenceRunner(
    private val imagePreprocessor: ImagePreprocessor,
    private val model: MnistModel
) {

    fun run(frame: Bitmap): PredictionResult? {
        val input = imagePreprocessor.preProcessForModel(frame)
        val prediction = model.predict(input) ?: return null

        return PredictionResult(
            predictedNumber = prediction.predictedClass,
            confidence = prediction.confidence,
            frame = frame
        )
    }

    fun close() {
        model.close()
    }
}
