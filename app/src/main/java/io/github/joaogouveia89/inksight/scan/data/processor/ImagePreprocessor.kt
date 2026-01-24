package io.github.joaogouveia89.inksight.scan.data.processor

import android.graphics.Bitmap
import androidx.core.graphics.scale
import io.github.joaogouveia89.inksight.ktx.asByteArray
import io.github.joaogouveia89.inksight.ktx.crop
import io.github.joaogouveia89.inksight.scan.data.model.CropMeasurements
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
        val pixels = imageBytes.map { (it.toInt() and 0xFF) }

        val threshold = calculateOtsuThreshold(pixels)

        val input = Array(1) {
            FloatArray(targetWidth * targetHeight) { i ->
                // Using Otsu's threshold to separate digit from background.
                // Assuming dark ink (foreground) < threshold.
                if (pixels[i] < threshold) {
                    pixels[i] / 255.0f
                } else 0f
            }
        }
        return input
    }

    /**
     * Implementation of Otsu's Method to find the optimal threshold.
     */
    private fun calculateOtsuThreshold(pixels: List<Int>): Int {
        val histogram = IntArray(256)
        for (p in pixels) histogram[p]++

        val total = pixels.size
        var sum = 0.0
        for (i in 0..255) sum += (i * histogram[i]).toDouble()

        var sumB = 0.0
        var wB = 0
        var wF: Int
        var maxVar = -1.0
        var threshold = 128

        for (i in 0..255) {
            wB += histogram[i]
            if (wB == 0) continue
            wF = total - wB
            if (wF == 0) break

            sumB += (i * histogram[i]).toDouble()
            val mB = sumB / wB
            val mF = (sum - sumB) / wF

            val varBetween = wB.toDouble() * wF.toDouble() * (mB - mF) * (mB - mF)
            if (varBetween > maxVar) {
                maxVar = varBetween
                threshold = i
            }
        }
        return threshold
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
