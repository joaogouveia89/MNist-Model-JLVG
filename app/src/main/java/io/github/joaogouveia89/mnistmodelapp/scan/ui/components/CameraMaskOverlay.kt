package io.github.joaogouveia89.mnistmodelapp.scan.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color

@Composable
fun CameraMaskOverlay(maskSize: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val overlayColor = Color.Black.copy(alpha = 0.6f)

        val rectWidth = size.width * maskSize

        val left = (size.width - rectWidth) / 2f
        val top = (size.height - rectWidth) / 2f
        val right = left + rectWidth
        val bottom = top + rectWidth

        val holeRect = Rect(left, top, right, bottom)


        drawRect(
            color = overlayColor,
            size = size,
            blendMode = BlendMode.SrcOver
        )

        drawRect(
            color = Color.Transparent,
            topLeft = holeRect.topLeft,
            size = holeRect.size,
            blendMode = BlendMode.Clear
        )
    }
}