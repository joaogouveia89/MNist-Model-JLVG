package io.github.joaogouveia89.mnistmodelapp.helpers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap

class BitmapUtils {
    companion object {
        fun createMockBitmap(text: String): Bitmap = createBitmap(100, 100).apply {
            val canvas = Canvas(this)
            canvas.drawColor(Color.LightGray.toArgb())
            val paint = Paint().apply {
                color = Color.Black.toArgb()
                textSize = 60f
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }
            canvas.drawText(text, 50f, 70f, paint)
        }
    }
}