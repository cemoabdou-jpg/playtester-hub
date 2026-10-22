package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class ThemeMode(val titleAr: String, val titleEn: String, val icon: String) {
    SYSTEM("تلقائي", "System", "⚙️"),
    LIGHT("فاتح", "Light", "☀️"),
    DARK("داكن", "Dark", "🌙")
}

enum class ThemePalette(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val darkPrimaryColor: Color,
    val accentColor: Color,
    val descriptionAr: String
) {
    PLAY_GREEN(
        id = "play_green",
        titleAr = "أخضر متجر بلاي",
        titleEn = "Play Green",
        primaryColor = Color(0xFF00875A),
        secondaryColor = Color(0xFF00A86B),
        darkPrimaryColor = Color(0xFF00C853),
        accentColor = Color(0xFFFFB300),
        descriptionAr = "المظهر الكلاسيكي المعتمد لمتجر جوجل بلاي"
    ),
    DEEP_OCEAN(
        id = "deep_ocean",
        titleAr = "أزرق المحيط التقني",
        titleEn = "Ocean Cyber",
        primaryColor = Color(0xFF1E88E5),
        secondaryColor = Color(0xFF0288D1),
        darkPrimaryColor = Color(0xFF42A5F5),
        accentColor = Color(0xFF26C6DA),
        descriptionAr = "طابع برمجي وهندسي احترافي هادئ"
    ),
    SUNSET_GOLD(
        id = "sunset_gold",
        titleAr = "الذهبي الملكي وعسلي النجوم",
        titleEn = "Sunset Gold",
        primaryColor = Color(0xFFD97706),
        secondaryColor = Color(0xFFF59E0B),
        darkPrimaryColor = Color(0xFFFFB300),
        accentColor = Color(0xFF10B981),
        descriptionAr = "طابع الأوسمة الذهبية ولوحة المتصدرين"
    ),
    NEON_VIOLET(
        id = "neon_violet",
        titleAr = "البنفسجي النيوني",
        titleEn = "Neon Violet",
        primaryColor = Color(0xFF7C3AED),
        secondaryColor = Color(0xFFA855F7),
        darkPrimaryColor = Color(0xFFC084FC),
        accentColor = Color(0xFFF43F5E),
        descriptionAr = "مظهر عصري للألعاب وتطبيقات المستقبل"
    ),
    CRIMSON_RUBY(
        id = "crimson_ruby",
        titleAr = "الياقوت القرمزي",
        titleEn = "Crimson Ruby",
        primaryColor = Color(0xFFDC2626),
        secondaryColor = Color(0xFFF43F5E),
        darkPrimaryColor = Color(0xFFFB7185),
        accentColor = Color(0xFFF59E0B),
        descriptionAr = "مظهر جريء وعالي الطاقة للمطورين"
    ),
    NORDIC_TEAL(
        id = "nordic_teal",
        titleAr = "التركواز والنعناع",
        titleEn = "Nordic Teal",
        primaryColor = Color(0xFF0D9488),
        secondaryColor = Color(0xFF14B8A6),
        darkPrimaryColor = Color(0xFF2DD4BF),
        accentColor = Color(0xFF6366F1),
        descriptionAr = "ألوان هادئة ونقية ومريحة للعينين"
    )
}

object ThemeManager {
    private const val PREFS_NAME = "playtester_theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_PALETTE_ID = "theme_palette_id"
    private const val KEY_AMOLED_BLACK = "theme_amoled_black"

    private var prefs: SharedPreferences? = null

    var currentThemeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    var currentPalette by mutableStateOf(ThemePalette.PLAY_GREEN)
        private set

    var isAmoledBlack by mutableStateOf(false)
        private set

    fun init(context: Context) {
        try {
            if (prefs != null) return
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs?.let { p ->
                val modeName = p.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
                currentThemeMode = try {
                    ThemeMode.valueOf(modeName ?: ThemeMode.SYSTEM.name)
                } catch (_: Throwable) {
                    ThemeMode.SYSTEM
                }

                val paletteId = p.getString(KEY_PALETTE_ID, ThemePalette.PLAY_GREEN.id)
                currentPalette = ThemePalette.values().firstOrNull { it.id == paletteId } ?: ThemePalette.PLAY_GREEN

                isAmoledBlack = p.getBoolean(KEY_AMOLED_BLACK, false)
            }
        } catch (_: Throwable) {}
    }

    fun setThemeMode(mode: ThemeMode) {
        currentThemeMode = mode
        prefs?.edit()?.putString(KEY_THEME_MODE, mode.name)?.apply()
    }

    fun setPalette(palette: ThemePalette) {
        currentPalette = palette
        prefs?.edit()?.putString(KEY_PALETTE_ID, palette.id)?.apply()
    }

    fun updateAmoledBlack(enabled: Boolean) {
        isAmoledBlack = enabled
        prefs?.edit()?.putBoolean(KEY_AMOLED_BLACK, enabled)?.apply()
    }
}
