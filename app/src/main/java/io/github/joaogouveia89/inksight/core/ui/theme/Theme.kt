package io.github.joaogouveia89.inksight.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SteelBlue,
    onPrimary = Color.White,
    primaryContainer = PrussianBlue,
    onPrimaryContainer = Color.White,
    secondary = CharcoalBlue,
    onSecondary = Color.White,
    tertiary = AccentYellow,
    onTertiary = PrussianBlueDark,
    background = PrussianBlueDark,
    onBackground = Color.White,
    surface = DeepSpaceBlue,
    onSurface = Color.White,
    error = ErrorRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SteelBlue,
    onPrimary = Color.White,
    primaryContainer = LightSkyBlue,
    onPrimaryContainer = PrussianBlue,
    secondary = DeepSpaceBlue,
    onSecondary = Color.White,
    tertiary = CharcoalBlue,
    background = Color(0xFFFAFAFA),
    onBackground = PrussianBlueDark,
    surface = Color.White,
    onSurface = PrussianBlueDark,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun MNistModelAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled by default to respect the brand identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}