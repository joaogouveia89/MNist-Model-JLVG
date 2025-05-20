package io.github.joaogouveia89.mnistmodelapp.ktx

import android.graphics.Bitmap
import android.media.Image
import androidx.core.graphics.createBitmap
import java.nio.ByteBuffer

fun Image.toBitmap(): Bitmap {
    val yPlane = planes[0]
    val yBuffer = yPlane.buffer
    val rowStride = yPlane.rowStride
    val pixelStride = yPlane.pixelStride
    val width = width
    val height = height

    val bitmap = createBitmap(width, height, Bitmap.Config.ALPHA_8)
    val dest = ByteArray(width * height)

    var pos = 0
    for (row in 0 until height) {
        val rowStart = row * rowStride
        for (col in 0 until width) {
            val index = rowStart + col * pixelStride
            dest[pos++] = yBuffer.get(index)
        }
    }

    bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(dest))
    return bitmap
}