package com.example.ui.theme

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
    primary = CrimsonRedLight,
    onPrimary = Color.White,
    primaryContainer = CrimsonRedDark,
    onPrimaryContainer = Color.White,
    secondary = SilverAccent,
    onSecondary = Color.Black,
    tertiary = CrimsonRed,
    onTertiary = Color.White,
    background = DarkNavyBg,
    onBackground = TextPrimaryDark,
    surface = DarkNavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkNavyBorder,
    outlineVariant = Color(0xFF52525B)
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEE2E2),
    onPrimaryContainer = CrimsonRedDark,
    secondary = SlateGray,
    onSecondary = Color.White,
    tertiary = CrimsonRedLight,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightCard,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = LightBorderVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
