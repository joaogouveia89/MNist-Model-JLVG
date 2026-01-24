package io.github.joaogouveia89.inksight.scan.data.model

import android.graphics.Bitmap

data class ProcessedFrame(
    val bitmap: Bitmap,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProcessedFrame

        if (bitmap != other.bitmap) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bitmap.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}