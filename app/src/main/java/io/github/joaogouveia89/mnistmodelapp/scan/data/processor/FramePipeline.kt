package io.github.joaogouveia89.mnistmodelapp.scan.data.processor

import android.graphics.Bitmap
import io.github.joaogouveia89.mnistmodelapp.ktx.asByteArray
import io.github.joaogouveia89.mnistmodelapp.ktx.rotateBitmap
import io.github.joaogouveia89.mnistmodelapp.scan.data.model.CropMeasurements
import io.github.joaogouveia89.mnistmodelapp.scan.data.model.ProcessedFrame

class FramePipeline(
    private val imagePreprocessor: ImagePreprocessor,
    private val maskSize: Float
) {
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


