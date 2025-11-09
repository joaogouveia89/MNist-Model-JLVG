package io.github.joaogouveia89.mnistmodelapp

import android.content.Context
import android.graphics.Bitmap
import io.github.joaogouveia89.mnistmodelapp.ktx.asByteArray
import io.github.joaogouveia89.mnistmodelapp.ktx.rotateBitmap

/**
 * Coordinates camera frame processing
 * Orchestrates ImagePreprocessor, HistogramAnalyzer, and MnistModel
 */

private const val TARGET_FPS: Int = 5 // 5 FPS

class FrameManager(
    context: Context,
    val maskSize: Float = 0.4f
) {
    private val imagePreprocessor = ImagePreprocessor()
    private val histogramAnalyzer = HistogramAnalyzer()
    private val mnistModel = MnistModel(context)

    private val minIntervalMs: Long = 1000L / TARGET_FPS
    private var lastPredictionTime = 0L

    private var cropMeasurements: CropMeasurements = CropMeasurements()

    suspend fun predictFrame(frame: Bitmap): PredictionResult? {

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPredictionTime < minIntervalMs) {
            return null
        }

        lastPredictionTime = currentTime

        val frameToAnalysis = frame.rotateBitmap(90f)

        if (cropMeasurements.isNotInitialized()) {
            cropMeasurements = imagePreprocessor.calculateCropMeasurements(
                frameWidth = frameToAnalysis.width,
                frameHeight = frameToAnalysis.height,
                maskSize = maskSize
            )
        }

        val cropped = imagePreprocessor.cropImage(frameToAnalysis, cropMeasurements)

        val imageBytes = cropped.asByteArray()
        val histogram = histogramAnalyzer.generateHistogram(imageBytes)
        val hasSignificantChange = histogramAnalyzer.isSignificantChange(histogram)

        if (!hasSignificantChange) {
            return null
        }

        val modelInput = imagePreprocessor.preProcessForModel(cropped)

        val modelPrediction = mnistModel.predict(modelInput) ?: return null

        return PredictionResult(
            predictedNumber = modelPrediction.predictedClass,
            confidence = modelPrediction.confidence,
            frame = cropped
        )
    }

    fun reset() {
        histogramAnalyzer.reset()
        cropMeasurements = CropMeasurements()
    }

    fun release() {
        mnistModel.close()
    }
}

data class PredictionResult(
    val predictedNumber: Int,
    val confidence: Float,
    val frame: Bitmap
)