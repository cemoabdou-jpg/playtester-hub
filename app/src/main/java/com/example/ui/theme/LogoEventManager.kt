package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import java.util.Calendar

enum class LogoEvent(
    val id: String,
    val titleAr: String,
    val subtitleAr: String,
    val badgeEmoji: String,
    val primaryColor: Color,
    val glowColor: Color,
    val secondaryColor: Color
) {
    AUTO(
        id = "auto",
        titleAr = "تلقائي حسب المناسبة والتقويم",
        subtitleAr = "يكتشف الأعياد والمواسم والأيام المميزة تلقائياً",
        badgeEmoji = "✨",
        primaryColor = Color(0xFF00875A),
        glowColor = Color(0xFF00E676),
        secondaryColor = Color(0xFFFFD54F)
    ),
    RAMADAN(
        id = "ramadan",
        titleAr = "رمضان المبارك",
        subtitleAr = "هلال ذهبي وفانوس مضيء ونجوم متلألئة",
        badgeEmoji = "🌙",
        primaryColor = Color(0xFFFFB300),
        glowColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFF00875A)
    ),
    EID(
        id = "eid",
        titleAr = "أفراح العيد والمناسبات",
        subtitleAr = "ألعاب نارية واحتفالية بهيجة ومفرقعات",
        badgeEmoji = "🎉",
        primaryColor = Color(0xFFE91E63),
        glowColor = Color(0xFFFF4081),
        secondaryColor = Color(0xFFFFD700)
    ),
    GOLD_RUSH(
        id = "gold_rush",
        titleAr = "موسم مضاعفة النقاط والعملات",
        subtitleAr = "هالة ذهبية نارية وعملات تطفو بنشاط",
        badgeEmoji = "🪙",
        primaryColor = Color(0xFFFF9100),
        glowColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFFF6D00)
    ),
    TECH_RADAR(
        id = "tech_radar",
        titleAr = "رادار الاختبار الذكي (14 يوماً)",
        subtitleAr = "أمواج مسح زرقاء وتقنية نيون مستقبلية",
        badgeEmoji = "⚡",
        primaryColor = Color(0xFF00B0FF),
        glowColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFF76FF03)
    ),
    ROYAL_OWNER(
        id = "royal_owner",
        titleAr = "التاج الملكي والدرع الموثق",
        subtitleAr = "هالة أرجوانية ملكية مع بريق التاج الذهبي",
        badgeEmoji = "👑",
        primaryColor = Color(0xFF7C3AED),
        glowColor = Color(0xFFA855F7),
        secondaryColor = Color(0xFFFFD700)
    ),
    NEW_YEAR(
        id = "new_year",
        titleAr = "احتفال العام والثلج الماسي",
        subtitleAr = "بلورات ثلجية وبريق ماسي متلألئ",
        badgeEmoji = "❄️",
        primaryColor = Color(0xFF00BCD4),
        glowColor = Color(0xFFE0F7FA),
        secondaryColor = Color(0xFF80DEEA)
    )
}

object LogoEventManager {
    private const val PREFS_NAME = "playtester_logo_event_prefs"
    private const val KEY_SELECTED_EVENT = "selected_logo_event"

    private var prefs: SharedPreferences? = null

    var selectedEvent by mutableStateOf(LogoEvent.AUTO)
        private set

    fun init(context: Context) {
        try {
            if (prefs != null) return
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs?.let { p ->
                val savedId = p.getString(KEY_SELECTED_EVENT, LogoEvent.AUTO.id)
                selectedEvent = LogoEvent.values().firstOrNull { it.id == savedId } ?: LogoEvent.AUTO
            }
        } catch (_: Throwable) {}
    }

    fun setEvent(event: LogoEvent) {
        selectedEvent = event
        prefs?.edit()?.putString(KEY_SELECTED_EVENT, event.id)?.apply()
    }

    /**
     * Determines the live active event (evaluating AUTO against calendar and days)
     */
    fun getEffectiveEvent(): LogoEvent {
        if (selectedEvent != LogoEvent.AUTO) return selectedEvent

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) // 0-based: 0 = Jan, 1 = Feb, 2 = Mar...
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Ramadan / Eid approximation
        if ((month == Calendar.FEBRUARY && dayOfMonth >= 20) || (month == Calendar.MARCH && dayOfMonth <= 30)) {
            return LogoEvent.RAMADAN
        }
        if ((month == Calendar.MARCH && dayOfMonth > 28) || (month == Calendar.APRIL && dayOfMonth <= 7)) {
            return LogoEvent.EID
        }
        if ((month == Calendar.DECEMBER && dayOfMonth >= 24) || (month == Calendar.JANUARY && dayOfMonth <= 7)) {
            return LogoEvent.NEW_YEAR
        }
        // Weekend Coin Rush (Thursday night, Friday, Saturday)
        if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
            return LogoEvent.GOLD_RUSH
        }

        return LogoEvent.TECH_RADAR
    }
}
