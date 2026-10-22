package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun PlayTesterTheme(
    darkTheme: Boolean = when (ThemeManager.currentThemeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    },
    palette: ThemePalette = ThemeManager.currentPalette,
    content: @Composable () -> Unit
) {
    val isAmoled = ThemeManager.isAmoledBlack && darkTheme

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.darkPrimaryColor,
            secondary = palette.secondaryColor,
            tertiary = palette.accentColor,
            background = if (isAmoled) Color(0xFF000000) else DarkBackground,
            surface = if (isAmoled) Color(0xFF0E1117) else DarkSurface,
            surfaceVariant = if (isAmoled) Color(0xFF1A1F26) else DarkSurfaceVariant,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onBackground = Color(0xFFE8EAED),
            onSurface = Color(0xFFE8EAED),
            onSurfaceVariant = Color(0xFFC4C7C5),
            outline = Color(0xFF44474F)
        )
    } else {
        lightColorScheme(
            primary = palette.primaryColor,
            secondary = palette.secondaryColor,
            tertiary = palette.accentColor,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.White,
            onBackground = Color(0xFF1A1C1E),
            onSurface = Color(0xFF1A1C1E),
            onSurfaceVariant = Color(0xFF44474F),
            outline = Color(0xFF74777F)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
