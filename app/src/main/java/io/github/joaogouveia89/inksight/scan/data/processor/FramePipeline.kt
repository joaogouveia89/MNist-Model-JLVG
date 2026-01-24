package io.github.joaogouveia89.inksight.scan.data.processor

import android.graphics.Bitmap
import io.github.joaogouveia89.inksight.ktx.asByteArray
import io.github.joaogouveia89.inksight.ktx.rotateBitmap
import io.github.joaogouveia89.inksight.scan.data.model.CropMeasurements
import io.github.joaogouveia89.inksight.scan.data.model.ProcessedFrame
import io.github.joaogouveia89.inksight.scan.domain.FrameAnalysisConfig
import javax.inject.Inject

class FramePipeline @Inject constructor(
    private val imagePreprocessor: ImagePreprocessor
) {
    private val maskSize: Float = FrameAnalysisConfig.MASK_SIZE
    private var cropMeasurements: CropMeasurements = CropMeasurements()

    fun process(frame: Bitmap): ProcessedFrame {
        val rotated = frame.rotateBitmap(90f)

        if (cropMeasurements.isNotInitialized()) {
            cropMeasurements = imagePreprocessor.calculateCropMeasurements(
                rotated.width,
                rotated.height,
                maskSize
            )
        }

        val cropped = imagePreprocessor.cropImage(rotated, cropMeasurements)
        val bytes = cropped.asByteArray()

        return ProcessedFrame(
            bitmap = cropped,
            bytes = bytes
        )
    }

    fun reset() {
        cropMeasurements = CropMeasurements()
    }
}
