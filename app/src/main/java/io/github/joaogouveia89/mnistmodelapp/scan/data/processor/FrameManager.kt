package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import android.content.Context
import android.graphics.Bitmap
import io.github.joaogouveia89.mnistmodelapp.ktx.asByteArray
import io.github.joaogouveia89.mnistmodelapp.ktx.rotateBitmap
import io.github.joaogouveia89.mnistmodelapp.scan.data.ml.MnistModel
import io.github.joaogouveia89.mnistmodelapp.scan.data.model.CropMeasurements
import io.github.joaogouveia89.mnistmodelapp.scan.domain.PredictionResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.math.abs

/**
 * Coordinates camera frame processing
 * Orchestrates ImagePreprocessor, HistogramAnalyzer, and MnistModel
 */

private const val TARGET_FPS: Int = 5 // 5 FPS
private const val STABILITY_WINDOW_SIZE = 10 // Last 10 frames
private const val STABILITY_THRESHOLD = 0.02 // 2% maximum variation between histograms

class FrameManager(
    context: Context,
    val maskSize: Float = 0.4f
) {
    private val imagePreprocessor = ImagePreprocessor()
    private val histogramAnalyzer = HistogramAnalyzer(
        differenceThreshold = 5000.0,
        stabilityWindowSize = STABILITY_WINDOW_SIZE,
        stabilityThreshold = STABILITY_THRESHOLD
    )
    private val mnistModel = MnistModel(context)

    private val minIntervalMs: Long = 1000L / TARGET_FPS
    private var lastPredictionTime = 0L

    private var cropMeasurements: CropMeasurements = CropMeasurements()

    private val _predictions = MutableSharedFlow<PredictionResult?>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val predictions = _predictions.asSharedFlow()

    suspend fun processFrame(frame: Bitmap) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPredictionTime < minIntervalMs) {
            return
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

        // Adds to the stability buffer
        histogramAnalyzer.addToStabilityBuffer(histogram)

        // Check if there is significant change (movement).
        val hasSignificantChange = histogramAnalyzer.isSignificantChange(histogram)

        if (!hasSignificantChange) {
            return
        }

        // It only processes if it's stable.
        if (!histogramAnalyzer.isStable()) {
            return
        }

        val modelInput = imagePreprocessor.preProcessForModel(cropped)

        val modelPrediction = mnistModel.predict(modelInput) ?: return

        val result = PredictionResult(
            predictedNumber = modelPrediction.predictedClass,
            confidence = modelPrediction.confidence,
            frame = cropped
        )

        _predictions.emit(result)
    }

    fun reset() {
        histogramAnalyzer.reset()
        cropMeasurements = CropMeasurements()
    }

    fun release() {
        mnistModel.close()
    }
}