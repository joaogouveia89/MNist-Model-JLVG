package io.github.joaogouveia89.inksight.digit_recognition.data.processor

import android.graphics.Bitmap
import androidx.core.graphics.scale
import io.github.joaogouveia89.inksight.ktx.asByteArray
import io.github.joaogouveia89.inksight.ktx.crop
import io.github.joaogouveia89.inksight.digit_recognition.data.model.CropMeasurements
import io.github.joaogouveia89.inksight.digit_recognition.data.model.ModelInputData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import androidx.core.graphics.createBitmap

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

    suspend fun preProcessForModel(
        cropped: Bitmap,
        targetWidth: Int = 28,
        targetHeight: Int = 28
    ): ModelInputData = withContext(Dispatchers.Default) {
        val intermediateWidth = targetWidth * 4
        val intermediateHeight = targetHeight * 4

        // 1. Scale to intermediate resolution to preserve stroke density
        val intermediate = cropped.scale(intermediateWidth, intermediateHeight, true)

        // 2. Apply adaptive thresholding to get clean ink data
        val binarizedPixels = binarize(intermediate)

        // 3. Create high-res binarized bitmap for the final downscale
        val binarizedBitmap = createBinarizedBitmap(binarizedPixels, intermediateWidth, intermediateHeight)

        // 4. Final scale to model input size (28x28) with anti-aliasing
        val finalBitmap = binarizedBitmap.scale(targetWidth, targetHeight, true)

        // 5. Prepare normalized float array for the TFLite model
        val modelInput = prepareModelInput(finalBitmap, targetWidth, targetHeight)

        ModelInputData(modelInput, finalBitmap)
    }

    private fun binarize(bitmap: Bitmap): ByteArray {
        val pixels = bitmap.asByteArray().map { it.toInt() and 0xFF }
        return applyAdaptiveThreshold(
            pixels = pixels,
            width = bitmap.width,
            height = bitmap.height,
            windowSize = 15,
            percentage = 12
        )
    }

    private fun createBinarizedBitmap(pixels: ByteArray, width: Int, height: Int): Bitmap {
        val bitmap = createBitmap(width, height, Bitmap.Config.ALPHA_8)
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(pixels))
        return bitmap
    }

    private fun prepareModelInput(bitmap: Bitmap, width: Int, height: Int): Array<FloatArray> {
        val pixels = bitmap.asByteArray().map { it.toInt() and 0xFF }
        return Array(1) {
            FloatArray(width * height) { i ->
                pixels[i] / 255.0f
            }
        }
    }

    /**
     * Adaptive thresholding (Bradley-Roth algorithm)
     */
    private fun applyAdaptiveThreshold(
        pixels: List<Int>,
        width: Int,
        height: Int,
        windowSize: Int,
        percentage: Int
    ): ByteArray {
        val output = ByteArray(pixels.size)
        val integralImage = IntArray(pixels.size)

        for (i in 0 until width) {
            var sum = 0
            for (j in 0 until height) {
                sum += pixels[j * width + i]
                integralImage[j * width + i] = if (i == 0) sum else integralImage[j * width + i - 1] + sum
            }
        }

        val s2 = windowSize / 2
        for (i in 0 until width) {
            for (j in 0 until height) {
                val x1 = max(i - s2, 0)
                val x2 = min(i + s2, width - 1)
                val y1 = max(j - s2, 0)
                val y2 = min(j + s2, height - 1)

                val count = (x2 - x1 + 1) * (y2 - y1 + 1)
                val sum = integralImage[y2 * width + x2] -
                        (if (x1 > 0) integralImage[y2 * width + x1 - 1] else 0) -
                        (if (y1 > 0) integralImage[(y1 - 1) * width + x2] else 0) +
                        (if (x1 > 0 && y1 > 0) integralImage[(y1 - 1) * width + x1 - 1] else 0)

                if (pixels[j * width + i] * 100 < sum * (100 - percentage) / count) {
                    output[j * width + i] = 255.toByte()
                } else {
                    output[j * width + i] = 0.toByte()
                }
            }
        }
        return output
    }

    fun calculateCropMeasurements(
        frameWidth: Int,
        frameHeight: Int,
        maskSize: Float
    ): CropMeasurements {
        val size = (min(frameWidth, frameHeight) * maskSize).toInt()
        return CropMeasurements(
            size = size,
            left = (frameWidth - size) / 2,
            top = (frameHeight - size) / 2
        )
    }
}
