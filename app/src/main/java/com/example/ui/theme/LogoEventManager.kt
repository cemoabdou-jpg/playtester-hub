package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import android.icu.util.IslamicCalendar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.ui.util.L
import java.util.Calendar

enum class LogoEvent(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val subtitleEn: String,
    val badgeEmoji: String,
    val primaryColor: Color,
    val glowColor: Color,
    val secondaryColor: Color
) {
    AUTO(
        id = "auto",
        titleAr = "تلقائي حسب المناسبة والتقويم",
        titleEn = "Auto by Season & Calendar",
        subtitleAr = "يكتشف الأعياد والمواسم والأيام المميزة تلقائياً",
        subtitleEn = "Automatically detects holidays, seasons, and special days",
        badgeEmoji = "🗓️",
        primaryColor = Color(0xFF00875A),
        glowColor = Color(0xFF00E676),
        secondaryColor = Color(0xFFFFD54F)
    ),
    RAMADAN(
        id = "ramadan",
        titleAr = "رمضان المبارك",
        titleEn = "Blessed Ramadan",
        subtitleAr = "هلال ذهبي وفانوس مضيء ونجوم متلألئة",
        subtitleEn = "Golden crescent, glowing lantern, and twinkling stars",
        badgeEmoji = "🌙",
        primaryColor = Color(0xFFFFB300),
        glowColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFF00875A)
    ),
    EID(
        id = "eid",
        titleAr = "أفراح العيد والمناسبات",
        titleEn = "Eid & Celebrations",
        subtitleAr = "ألعاب نارية واحتفالية بهيجة ومفرقعات",
        subtitleEn = "Festive fireworks, confetti, and joyful sparkles",
        badgeEmoji = "🎉",
        primaryColor = Color(0xFFE91E63),
        glowColor = Color(0xFFFF4081),
        secondaryColor = Color(0xFFFFD700)
    ),
    GOLD_RUSH(
        id = "gold_rush",
        titleAr = "موسم مضاعفة النقاط والعملات",
        titleEn = "Double Coin Rush Season",
        subtitleAr = "هالة ذهبية نارية وعملات تطفو بنشاط",
        subtitleEn = "Fiery golden aura with floating reward coins",
        badgeEmoji = "🪙",
        primaryColor = Color(0xFFFF9100),
        glowColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFFF6D00)
    ),
    TECH_RADAR(
        id = "tech_radar",
        titleAr = "رادار اختبار الـ 14 يوماً",
        titleEn = "14-Day Test Radar",
        subtitleAr = "أمواج مسح زرقاء وتقنية نيون مستقبلية",
        subtitleEn = "Blue radar waves with futuristic neon accents",
        badgeEmoji = "⚡",
        primaryColor = Color(0xFF00B0FF),
        glowColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFF76FF03)
    ),
    ROYAL_OWNER(
        id = "royal_owner",
        titleAr = "التاج الملكي والدرع الموثق",
        titleEn = "Royal Crown & Verified Shield",
        subtitleAr = "هالة أرجوانية ملكية مع بريق التاج الذهبي",
        subtitleEn = "Royal purple aura with shimmering golden crown",
        badgeEmoji = "👑",
        primaryColor = Color(0xFF7C3AED),
        glowColor = Color(0xFFA855F7),
        secondaryColor = Color(0xFFFFD700)
    ),
    NEW_YEAR(
        id = "new_year",
        titleAr = "احتفال العام والثلج الماسي",
        titleEn = "New Year & Diamond Snow",
        subtitleAr = "بلورات ثلجية وبريق ماسي متلألئ",
        subtitleEn = "Crystalline snowflakes and sparkling diamond glow",
        badgeEmoji = "❄️",
        primaryColor = Color(0xFF00BCD4),
        glowColor = Color(0xFFE0F7FA),
        secondaryColor = Color(0xFF80DEEA)
    );

    fun getTitle(): String = L.t(titleAr, titleEn)
    fun getSubtitle(): String = L.t(subtitleAr, subtitleEn)
}

object LogoEventManager {
    private const val PREFS_NAME = "playtester_logo_event_prefs"
    private const val KEY_SELECTED_EVENT = "selected_logo_event"
    private const val KEY_EXTERNAL_ICON_SYNC = "external_icon_sync_enabled"

    private var prefs: SharedPreferences? = null

    var selectedEvent by mutableStateOf(LogoEvent.AUTO)
        private set

    var externalIconSyncEnabled by mutableStateOf(true)
        private set

    fun init(context: Context) {
        try {
            if (prefs != null) return
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs?.let { p ->
                val savedId = p.getString(KEY_SELECTED_EVENT, LogoEvent.AUTO.id)
                selectedEvent = LogoEvent.values().firstOrNull { it.id == savedId } ?: LogoEvent.AUTO
                externalIconSyncEnabled = p.getBoolean(KEY_EXTERNAL_ICON_SYNC, true)
            }
        } catch (_: Throwable) {}
    }

    fun setExternalIconSync(enabled: Boolean, context: Context? = null) {
        externalIconSyncEnabled = enabled
        prefs?.edit()?.putBoolean(KEY_EXTERNAL_ICON_SYNC, enabled)?.commit()
        if (context != null) {
            if (enabled) {
                LauncherIconManager.syncWithEvent(context, getEffectiveEvent())
            } else {
                LauncherIconManager.resetToDefault(context)
            }
        }
    }

    fun applySelection(event: LogoEvent, syncExternalIcon: Boolean, context: Context) {
        selectedEvent = event
        externalIconSyncEnabled = syncExternalIcon
        prefs?.edit()
            ?.putString(KEY_SELECTED_EVENT, event.id)
            ?.putBoolean(KEY_EXTERNAL_ICON_SYNC, syncExternalIcon)
            ?.commit()
        if (syncExternalIcon) {
            LauncherIconManager.syncWithEvent(
                context = context,
                event = if (event == LogoEvent.AUTO) getEffectiveEvent() else event
            )
        } else {
            LauncherIconManager.resetToDefault(context)
        }
    }

    fun setEvent(event: LogoEvent, context: Context? = null) {
        selectedEvent = event
        prefs?.edit()?.putString(KEY_SELECTED_EVENT, event.id)?.commit()
        if (context != null && externalIconSyncEnabled) {
            LauncherIconManager.syncWithEvent(
                context = context,
                event = if (event == LogoEvent.AUTO) getEffectiveEvent() else event
            )
        }
    }

    /**
     * Determines the live active event (evaluating AUTO against Hijri & Gregorian calendars and weekends).
     */
    fun getEffectiveEvent(): LogoEvent {
        if (selectedEvent != LogoEvent.AUTO) return selectedEvent

        // 1. Accurate Hijri Calendar detection (Ramadan, Eid al-Fitr, Eid al-Adha)
        try {
            val hijri = IslamicCalendar()
            val hijriMonth = hijri.get(IslamicCalendar.MONTH) // 0-based: Ramadan = 8, Shawwal = 9, Dhu al-Hijjah = 11
            val hijriDay = hijri.get(IslamicCalendar.DAY_OF_MONTH)

            if (hijriMonth == IslamicCalendar.RAMADAN) {
                return LogoEvent.RAMADAN
            }
            if (hijriMonth == IslamicCalendar.SHAWWAL && hijriDay in 1..5) {
                return LogoEvent.EID
            }
            if (hijriMonth == IslamicCalendar.DHU_AL_HIJJAH && hijriDay in 9..13) {
                return LogoEvent.EID
            }
        } catch (_: Throwable) {
            // Fallback handled below if ICU IslamicCalendar fails on any device
        }

        // 2. Gregorian seasonal events & weekly Gold Rush
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if ((month == Calendar.DECEMBER && dayOfMonth >= 24) || (month == Calendar.JANUARY && dayOfMonth <= 7)) {
            return LogoEvent.NEW_YEAR
        }

        // Weekend Coin Rush (Friday & Saturday)
        if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
            return LogoEvent.GOLD_RUSH
        }

        return LogoEvent.TECH_RADAR
    }
}
