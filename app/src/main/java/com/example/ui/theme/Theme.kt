package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode(val title: String, val description: String) {
    SYSTEM("تلقائي (حسب النظام)", "مزامنة المظهر تلقائياً مع وضع هاتفك"),
    LIGHT("الوضع الفاتح (نهاري)", "ألوان ناصعة وواضحة للإضاءة النهارية"),
    DARK("الوضع الداكن (ليلي)", "ألوان هادئة مريحة للعين في الإضاءة المنخفضة")
}

enum class AppColorTheme(
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val primaryColor: Color,
    val previewColor: Color
) {
    CALM_BLUE(
        "الأزرق الملكي الهادئ",
        "Calm Royal Blue",
        "ألوان هادئة ومريحة للنظر تقلل الإجهاد البصري",
        "Restful blue tones that reduce visual fatigue",
        Color(0xFF2563EB),
        Color(0xFF3B82F6)
    ),
    EMERALD_SAGE(
        "الأخضر الزمردي الطبيعي",
        "Emerald Sage",
        "طبيعي ومتوازن يمنح شعوراً بالراحة والاستقرار",
        "Organic forest green tones for visual calmness",
        Color(0xFF0F766E),
        Color(0xFF14B8A6)
    ),
    SLATE_MINIMAL(
        "الرمادي الحريري الهادئ",
        "Slate Minimal",
        "مظهر مقتصد فائق الهدوء وبساطة بدون تشتيت",
        "Ultra-minimalist slate palette with zero distractions",
        Color(0xFF334155),
        Color(0xFF64748B)
    ),
    LAVENDER_SOFT(
        "اللافندر الناعم",
        "Soft Lavender",
        "لمسة عصرية هادئة بألوان البنفسج الناعم",
        "Modern soothing purple and soft indigo tones",
        Color(0xFF4F46E5),
        Color(0xFF818CF8)
    )
}

fun getCustomColorScheme(
    darkTheme: Boolean,
    palette: AppColorTheme = AppColorTheme.CALM_BLUE
): ColorScheme {
    val primary = when (palette) {
        AppColorTheme.CALM_BLUE -> if (darkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB)
        AppColorTheme.EMERALD_SAGE -> if (darkTheme) Color(0xFF2DD4BF) else Color(0xFF0F766E)
        AppColorTheme.SLATE_MINIMAL -> if (darkTheme) Color(0xFF94A3B8) else Color(0xFF334155)
        AppColorTheme.LAVENDER_SOFT -> if (darkTheme) Color(0xFFA5B4FC) else Color(0xFF4F46E5)
    }

    val primaryContainer = when (palette) {
        AppColorTheme.CALM_BLUE -> if (darkTheme) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)
        AppColorTheme.EMERALD_SAGE -> if (darkTheme) Color(0xFF134E4A) else Color(0xFFCCFBF1)
        AppColorTheme.SLATE_MINIMAL -> if (darkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
        AppColorTheme.LAVENDER_SOFT -> if (darkTheme) Color(0xFF312E81) else Color(0xFFEEF2FF)
    }

    val onPrimaryContainer = when (palette) {
        AppColorTheme.CALM_BLUE -> if (darkTheme) Color(0xFFDBEAFE) else Color(0xFF1E3A8A)
        AppColorTheme.EMERALD_SAGE -> if (darkTheme) Color(0xFFCCFBF1) else Color(0xFF134E4A)
        AppColorTheme.SLATE_MINIMAL -> if (darkTheme) Color(0xFFF1F5F9) else Color(0xFF0F172A)
        AppColorTheme.LAVENDER_SOFT -> if (darkTheme) Color(0xFFEEF2FF) else Color(0xFF312E81)
    }

    val tertiary = when (palette) {
        AppColorTheme.CALM_BLUE -> if (darkTheme) Color(0xFF38BDF8) else Color(0xFF0284C7)
        AppColorTheme.EMERALD_SAGE -> if (darkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB)
        AppColorTheme.SLATE_MINIMAL -> if (darkTheme) Color(0xFF38BDF8) else Color(0xFF2563EB)
        AppColorTheme.LAVENDER_SOFT -> if (darkTheme) Color(0xFF2DD4BF) else Color(0xFF0D9488)
    }

    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color(0xFF0F172A),
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = SlateGrayLight,
            onSecondary = Color(0xFF0F172A),
            tertiary = tertiary,
            onTertiary = Color(0xFF0F172A),
            background = DarkNavyBg,
            onBackground = TextPrimaryDark,
            surface = DarkNavySurface,
            onSurface = TextPrimaryDark,
            surfaceVariant = DarkNavyCard,
            onSurfaceVariant = TextSecondaryDark,
            outline = DarkNavyBorder,
            outlineVariant = Color(0xFF334155)
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = SlateGray,
            onSecondary = Color.White,
            tertiary = tertiary,
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
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorTheme: AppColorTheme = AppColorTheme.CALM_BLUE,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> getCustomColorScheme(darkTheme = darkTheme, palette = colorTheme)
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
