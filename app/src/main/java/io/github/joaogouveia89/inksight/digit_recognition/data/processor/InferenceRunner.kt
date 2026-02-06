package io.github.joaogouveia89.inksight.digit_recognition.data.processor

import android.graphics.Bitmap
import io.github.joaogouveia89.inksight.digit_recognition.data.ml.MnistModel
import io.github.joaogouveia89.inksight.digit_recognition.domain.PredictionResult
import javax.inject.Inject

class InferenceRunner @Inject constructor(
    private val imagePreprocessor: ImagePreprocessor,
    private val model: MnistModel
) {

    suspend fun run(frame: Bitmap): PredictionResult? {
        val inputData = imagePreprocessor.preProcessForModel(frame)
        val prediction = model.predict(inputData.input) ?: return null

        return PredictionResult(
            predictedNumber = prediction.predictedClass,
            confidence = prediction.confidence,
            frame = inputData.binarizedBitmap
        )
    }

    fun close() {
        model.close()
    }
}
