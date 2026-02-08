package io.github.joaogouveia89.inksight.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSpacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val doubleExtraLarge: Dp = 48.dp,
    val tripleExtraLarge: Dp = 64.dp
)

val LocalSpacing = staticCompositionLocalOf { AppSpacing() }

// Legacy Dimens object (will be deprecated or kept for specific sizes)
object Dimens {
    val screenPaddingHorizontal = 24.dp
    val screenPaddingVertical = 16.dp
    val contentSpacingLarge = 24.dp
    val iconSizeLarge = 80.dp
    val buttonHeight = 56.dp
    val buttonCornerRadius = 12.dp
    val iconPaddingEnd = 8.dp
}
