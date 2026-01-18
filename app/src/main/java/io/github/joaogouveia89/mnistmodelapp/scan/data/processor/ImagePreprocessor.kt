package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import android.graphics.Bitmap
import androidx.core.graphics.scale
import io.github.joaogouveia89.mnistmodelapp.ktx.asByteArray
import io.github.joaogouveia89.mnistmodelapp.ktx.crop
import io.github.joaogouveia89.mnistmodelapp.scan.data.model.CropMeasurements
import javax.inject.Inject

/*
 * Prepare images for model analysis.
 */

class ImagePreprocessor @Inject constructor() {

    fun cropImage(
        bitmap: Bitmap,
        measurements: CropMeasurements
    ): Bitmap {
        return bitmap.crop(
            measurements.size,
            measurements.top,
            measurements.left
        )
    }

    fun preProcessForModel(
        cropped: Bitmap,
        targetWidth: Int = 28,
        targetHeight: Int = 28
    ): Array<FloatArray> {
        val scaledImage = cropped.scale(targetWidth, targetHeight, true)
        val imageBytes = scaledImage.asByteArray()
        val handleBytes = imageBytes.map { (it.toInt() and 0xFF) }
        val average = handleBytes.average()

        val input = Array(1) {
            FloatArray(targetWidth * targetHeight) { i ->
                val candidate = handleBytes[i] / 255.0f
                if (handleBytes[i] < average) {
                    candidate
                } else 0f
            }
        }
        return input
    }

    fun calculateCropMeasurements(
        frameWidth: Int,
        frameHeight: Int,
        maskSize: Float
    ): CropMeasurements {
        val size = (frameWidth * maskSize).toInt()
        return CropMeasurements(
            size = size,
            left = (frameWidth - size) / 2,
            top = (frameHeight - size) / 2
        )
    }
}
