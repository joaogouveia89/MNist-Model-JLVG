package io.github.joaogouveia89.mnistmodelapp.ktx

import android.graphics.Bitmap
import android.graphics.Matrix
import java.nio.ByteBuffer


fun Bitmap.rotateBitmap(angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.crop(cropSize: Int, cropTop: Int, cropLeft: Int): Bitmap =
    Bitmap.createBitmap(this, cropLeft, cropTop, cropSize, cropSize)


fun Bitmap.asByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(width * height)
    copyPixelsToBuffer(buffer)
    return buffer.array()
}