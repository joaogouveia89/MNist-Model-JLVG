package io.github.joaogouveia89.inksight.scan.data.processor

import android.graphics.Bitmap
import androidx.core.graphics.scale
import io.github.joaogouveia89.inksight.ktx.asByteArray
import io.github.joaogouveia89.inksight.ktx.crop
import io.github.joaogouveia89.inksight.scan.data.model.CropMeasurements
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

data class ModelInputData(
    val input: Array<FloatArray>,
    val binarizedBitmap: Bitmap
)

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
    ): ModelInputData {
        // Step 1: Scale to an intermediate resolution to preserve detail during binarization
        val processingWidth = targetWidth * 4
        val processingHeight = targetHeight * 4
        val intermediate = cropped.scale(processingWidth, processingHeight, true)
        
        val pixels = intermediate.asByteArray().map { it.toInt() and 0xFF }
        val binarized = applyAdaptiveThreshold(pixels, processingWidth, processingHeight)

        // Step 2: Create a high-res binarized bitmap to scale it down properly
        val highResBinarized = Bitmap.createBitmap(processingWidth, processingHeight, Bitmap.Config.ALPHA_8)
        highResBinarized.copyPixelsFromBuffer(ByteBuffer.wrap(binarized))

        // Step 3: Scale down to model size (28x28). 
        // Scaling a binarized image creates soft anti-aliased edges which the model likes.
        val finalBitmap = highResBinarized.scale(targetWidth, targetHeight, true)
        val finalPixels = finalBitmap.asByteArray().map { it.toInt() and 0xFF }

        val input = Array(1) {
            FloatArray(targetWidth * targetHeight) { i ->
                // Normalize to 0.0 - 1.0
                finalPixels[i] / 255.0f
            }
        }

        return ModelInputData(input, finalBitmap)
    }

    /**
     * Adaptive thresholding (Bradley-Roth algorithm)
     * Good for handling uneven lighting and shadows.
     */
    private fun applyAdaptiveThreshold(
        pixels: List<Int>,
        width: Int,
        height: Int,
        windowSize: Int = 15,
        percentage: Int = 15
    ): ByteArray {
        val output = ByteArray(pixels.size)
        val integralImage = IntArray(pixels.size)
        
        // Calculate integral image for fast local mean calculation
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

                // If pixel is significantly darker than surrounding mean, it's ink (set to white 255)
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
        val size = (frameWidth * maskSize).toInt()
        return CropMeasurements(
            size = size,
            left = (frameWidth - size) / 2,
            top = (frameHeight - size) / 2
        )
    }
}
