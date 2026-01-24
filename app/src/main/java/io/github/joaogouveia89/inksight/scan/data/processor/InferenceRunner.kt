package io.github.joaogouveia89.inksight.scan.data.processor

import android.graphics.Bitmap
import io.github.joaogouveia89.inksight.scan.data.ml.MnistModel
import io.github.joaogouveia89.inksight.scan.domain.PredictionResult
import javax.inject.Inject

class InferenceRunner @Inject constructor(
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
