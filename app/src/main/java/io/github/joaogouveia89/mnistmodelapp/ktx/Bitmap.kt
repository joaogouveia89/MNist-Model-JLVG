package io.github.joaogouveia89.mnistmodelapp.ktx

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
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

fun Bitmap.toBlackBg(valueToSubtract: Int): Bitmap{
    val mutableBitmap = if (this.isMutable) {
        this
    } else {
        this.config?.let {
            this.copy(it, true)
        } ?: this
    }

    // Convert to 0-1 range
    val subtractValue = valueToSubtract / 255f

    // Criar a ColorMatrix para subtrair o valor
    val colorMatrix = ColorMatrix(floatArrayOf(
        1f, 0f, 0f, 0f, -subtractValue, // Red: R = R - subtractValue
        0f, 1f, 0f, 0f, -subtractValue, // Green: G = G - subtractValue
        0f, 0f, 1f, 0f, -subtractValue, // Blue: B = B - subtractValue
        0f, 0f, 0f, 1f, 0f              // Alpha: A = A (no change)
    ))

    // Aplicar a matriz usando Paint e Canvas
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

    val canvas = Canvas(mutableBitmap)
    canvas.drawBitmap(mutableBitmap, 0f, 0f, paint)

    return mutableBitmap
}