package io.github.joaogouveia89.inksight.scan.data.model

import android.graphics.Bitmap

/**
 * Encapsulates the processed input for the ML model and its visual representation.
 */
data class ModelInputData(
    val input: Array<FloatArray>,
    val binarizedBitmap: Bitmap
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelInputData

        if (!input.contentDeepEquals(other.input)) return false
        if (binarizedBitmap != other.binarizedBitmap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = input.contentDeepHashCode()
        result = 31 * result + binarizedBitmap.hashCode()
        return result
    }
}
