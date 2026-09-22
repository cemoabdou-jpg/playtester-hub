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
    primary = PlayGreenLight,
    onPrimary = Color.Black,
    primaryContainer = PlayGreenDark,
    onPrimaryContainer = PlayGreenLight,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    tertiary = AmberGold,
    onTertiary = Color.Black,
    background = DarkNavyBg,
    onBackground = TextPrimaryDark,
    surface = DarkNavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF475569)
)

private val LightColorScheme = lightColorScheme(
    primary = PlayGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = PlayGreenDark,
    secondary = CyanAccent,
    onSecondary = Color.White,
    tertiary = AmberGold,
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
    dynamicColor: Boolean = false, // Use our custom cohesive developer branding
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

