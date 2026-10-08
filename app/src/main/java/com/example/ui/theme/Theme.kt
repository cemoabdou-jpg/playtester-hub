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
    LIGHT("الوضع الفاتح الهادئ (نهاري)", "ألوان فاتحة وهادئة وواضحة مريحة للعين"),
    DARK("الوضع الداكن (ليلي)", "ألوان هادئة مريحة للعين في الإضاءة المنخفضة"),
    SYSTEM("تلقائي (حسب النظام)", "مزامنة المظهر تلقائياً مع وضع هاتفك")
}

enum class AppColorTheme(
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val primaryColor: Color,
    val previewColor: Color
) {
    MINT_BEIGE(
        "بيج ومينت غرين (الافتراضي)",
        "Soft Mint & Beige",
        "تدرجات بيج كتاني طبيعية دافئة مع لمسات أخضر نعناعي عشبي مريح",
        "Calm warm beige linen with soothing refreshing mint green",
        Color(0xFF2E856E),
        Color(0xFF52A890)
    ),
    CALM_BLUE(
        "الأزرق الهادئ الفاتح",
        "Calm Soft Sky",
        "ألوان فاتحة وهادئة ومريحة للنظر تقلل الإجهاد البصري",
        "Light, soothing and restful sky tones that reduce visual fatigue",
        Color(0xFF3B82F6),
        Color(0xFF60A5FA)
    ),
    EMERALD_SAGE(
        "الأخضر الزمردي الطبيعي",
        "Emerald Sage",
        "طبيعي ومتوازن يمنح شعوراً بالراحة والاستقرار",
        "Organic natural green tones for visual calmness",
        Color(0xFF059669),
        Color(0xFF34D399)
    ),
    SLATE_MINIMAL(
        "الرمادي الحريري الهادئ",
        "Slate Minimal",
        "مظهر مقتصد فائق الهدوء وبساطة بدون تشتيت",
        "Ultra-minimalist soft slate palette with zero distractions",
        Color(0xFF475569),
        Color(0xFF94A3B8)
    )
}

fun getCustomColorScheme(
    darkTheme: Boolean,
    palette: AppColorTheme = AppColorTheme.MINT_BEIGE
): ColorScheme {
    val primary = when (palette) {
        AppColorTheme.MINT_BEIGE -> if (darkTheme) Color(0xFF52A890) else Color(0xFF2E856E)
        AppColorTheme.CALM_BLUE -> if (darkTheme) Color(0xFF60A5FA) else Color(0xFF2563EB)
        AppColorTheme.EMERALD_SAGE -> if (darkTheme) Color(0xFF34D399) else Color(0xFF0D9488)
        AppColorTheme.SLATE_MINIMAL -> if (darkTheme) Color(0xFF94A3B8) else Color(0xFF475569)
    }

    val primaryContainer = when (palette) {
        AppColorTheme.MINT_BEIGE -> if (darkTheme) Color(0xFF1E5C4D) else Color(0xFFE2EFE9)
        AppColorTheme.CALM_BLUE -> if (darkTheme) Color(0xFF1E3A8A) else Color(0xFFDBEAFE)
        AppColorTheme.EMERALD_SAGE -> if (darkTheme) Color(0xFF0F4A42) else Color(0xFFCCFBF1)
        AppColorTheme.SLATE_MINIMAL -> if (darkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    }

    val onPrimaryContainer = when (palette) {
        AppColorTheme.MINT_BEIGE -> if (darkTheme) Color(0xFFD1FAE5) else Color(0xFF134E48)
        AppColorTheme.CALM_BLUE -> if (darkTheme) Color(0xFFDBEAFE) else Color(0xFF1E40AF)
        AppColorTheme.EMERALD_SAGE -> if (darkTheme) Color(0xFFA7F3D0) else Color(0xFF115E59)
        AppColorTheme.SLATE_MINIMAL -> if (darkTheme) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    }

    val tertiary = when (palette) {
        AppColorTheme.MINT_BEIGE -> if (darkTheme) Color(0xFF52A890) else Color(0xFF267362)
        AppColorTheme.CALM_BLUE -> if (darkTheme) Color(0xFF38BDF8) else Color(0xFF0284C7)
        AppColorTheme.EMERALD_SAGE -> if (darkTheme) Color(0xFF6EE7B7) else Color(0xFF0F766E)
        AppColorTheme.SLATE_MINIMAL -> if (darkTheme) Color(0xFF38BDF8) else Color(0xFF64748B)
    }

    // Dynamic background and surface per selected color theme (distinct visual experience)
    val lightBg = when (palette) {
        AppColorTheme.MINT_BEIGE -> Color(0xFFE8E2D5)      // بيج كتاني حجري أغمق ودافئ ومريح
        AppColorTheme.CALM_BLUE -> Color(0xFFE2E8F0)       // رمادي أزرق هادئ وأنيق
        AppColorTheme.EMERALD_SAGE -> Color(0xFFE0E7E5)    // رمادي مريمي طبيعي مريح
        AppColorTheme.SLATE_MINIMAL -> Color(0xFFE2E5E9)   // رمادي كلاسيكي مقتصد ومريح
    }

    val lightSurface = when (palette) {
        AppColorTheme.MINT_BEIGE -> Color(0xFFF5F1E8)
        AppColorTheme.CALM_BLUE -> Color(0xFFF1F5F9)
        AppColorTheme.EMERALD_SAGE -> Color(0xFFEFF4F2)
        AppColorTheme.SLATE_MINIMAL -> Color(0xFFF1F3F5)
    }

    val lightCard = when (palette) {
        AppColorTheme.MINT_BEIGE -> Color(0xFFDDD5C6)
        AppColorTheme.CALM_BLUE -> Color(0xFFD8E1EC)
        AppColorTheme.EMERALD_SAGE -> Color(0xFFD4DEDC)
        AppColorTheme.SLATE_MINIMAL -> Color(0xFFD8DCE1)
    }

    val lightBorder = when (palette) {
        AppColorTheme.MINT_BEIGE -> Color(0xFFCCC3B3)
        AppColorTheme.CALM_BLUE -> Color(0xFFCBD5E1)
        AppColorTheme.EMERALD_SAGE -> Color(0xFFC4D1CF)
        AppColorTheme.SLATE_MINIMAL -> Color(0xFFCBD2DA)
    }

    val darkBg = when (palette) {
        AppColorTheme.MINT_BEIGE -> Color(0xFF0F1815)
        AppColorTheme.CALM_BLUE -> Color(0xFF0B1324)
        AppColorTheme.EMERALD_SAGE -> Color(0xFF0A1816)
        AppColorTheme.SLATE_MINIMAL -> Color(0xFF0F141C)
    }

    val darkSurface = when (palette) {
        AppColorTheme.MINT_BEIGE -> Color(0xFF182622)
        AppColorTheme.CALM_BLUE -> Color(0xFF142038)
        AppColorTheme.EMERALD_SAGE -> Color(0xFF132522)
        AppColorTheme.SLATE_MINIMAL -> Color(0xFF19202C)
    }

    val darkCard = when (palette) {
        AppColorTheme.MINT_BEIGE -> Color(0xFF20322D)
        AppColorTheme.CALM_BLUE -> Color(0xFF1E2D4A)
        AppColorTheme.EMERALD_SAGE -> Color(0xFF1D322E)
        AppColorTheme.SLATE_MINIMAL -> Color(0xFF232D3E)
    }

    val darkBorder = when (palette) {
        AppColorTheme.MINT_BEIGE -> Color(0xFF2D453E)
        AppColorTheme.CALM_BLUE -> Color(0xFF293B5F)
        AppColorTheme.EMERALD_SAGE -> Color(0xFF28443F)
        AppColorTheme.SLATE_MINIMAL -> Color(0xFF2F3C4F)
    }

    return if (darkTheme) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color(0xFF0C1412),
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = SlateGrayLight,
            onSecondary = Color(0xFF0C1412),
            tertiary = tertiary,
            onTertiary = Color(0xFF0C1412),
            background = darkBg,
            onBackground = TextPrimaryDark,
            surface = darkSurface,
            onSurface = TextPrimaryDark,
            surfaceVariant = darkCard,
            onSurfaceVariant = TextSecondaryDark,
            outline = darkBorder,
            outlineVariant = darkBorder.copy(alpha = 0.7f)
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
            background = lightBg,
            onBackground = TextPrimaryLight,
            surface = lightSurface,
            onSurface = TextPrimaryLight,
            surfaceVariant = lightCard,
            onSurfaceVariant = TextSecondaryLight,
            outline = lightBorder,
            outlineVariant = lightBorder
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    colorTheme: AppColorTheme = AppColorTheme.MINT_BEIGE,
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
