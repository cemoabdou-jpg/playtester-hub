package com.example.ui.util

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*

enum class AppLanguage(val code: String, val displayName: String) {
    ARABIC("ar", "العربية"),
    ENGLISH("en", "English")
}

object L {
    private const val PREFS_NAME = "playtester_lang_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    private var prefs: SharedPreferences? = null

    var currentLanguage by mutableStateOf(AppLanguage.ARABIC)
        private set

    fun init(context: Context) {
        try {
            if (prefs != null) return
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs?.let { p ->
                val savedCode = p.getString(KEY_LANGUAGE, AppLanguage.ARABIC.code)
                currentLanguage = AppLanguage.values().firstOrNull { it.code == savedCode } ?: AppLanguage.ARABIC
            }
        } catch (_: Throwable) {}
    }

    fun setLanguage(language: AppLanguage) {
        currentLanguage = language
        prefs?.edit()?.putString(KEY_LANGUAGE, language.code)?.apply()
    }

    fun isArabic(): Boolean = currentLanguage == AppLanguage.ARABIC

    fun t(ar: String, en: String): String = if (isArabic()) ar else en

    fun localizeCategory(raw: String): String {
        val c = raw.trim().lowercase()
        return when {
            c.isEmpty() || c == "عام" || c == "general" || c == "تطبيق" || c == "app" ->
                t("عام", "General")
            c.contains("ألعاب") || c.contains("لعب") || c.contains("game") ->
                t("ألعاب", "Games")
            c.contains("أدوات") || c.contains("tool") ->
                t("أدوات", "Tools")
            c.contains("إنتاج") || c.contains("product") ->
                t("إنتاجية", "Productivity")
            c.contains("تواصل") || c.contains("social") || c.contains("chat") ->
                t("تواصل", "Social")
            c.contains("تعليم") || c.contains("edu") ->
                t("تعليم", "Education")
            c.contains("مال") || c.contains("أعمال") || c.contains("finance") || c.contains("business") ->
                t("مال وأعمال", "Finance & Business")
            c.contains("صحة") || c.contains("رياض") || c.contains("health") || c.contains("fit") ->
                t("صحة ورياضة", "Health & Fitness")
            c.contains("تسوق") || c.contains("متاجر") || c.contains("shop") ->
                t("تسوق ومتاجر", "Shopping")
            c.contains("ميديا") || c.contains("فيديو") || c.contains("media") || c.contains("video") ->
                t("ميديا وفيديو", "Media & Video")
            c.contains("تطوير") || c.contains("برمج") || c.contains("dev") || c.contains("code") ->
                t("تطوير وبرمجة", "Developer Tools")
            else -> raw
        }
    }

    fun isCategoryMatch(appCategory: String, filterKey: String): Boolean {
        val c = appCategory.trim().lowercase()
        return when (filterKey) {
            "ALL", "MOST_NEEDED" -> true
            "TOOLS" -> c.contains("أدوات") || c.contains("tool")
            "GAMES" -> c.contains("ألعاب") || c.contains("لعب") || c.contains("game")
            "PRODUCTIVITY" -> c.contains("إنتاج") || c.contains("product")
            "EDUCATION" -> c.contains("تعليم") || c.contains("edu")
            "SOCIAL" -> c.contains("تواصل") || c.contains("social")
            else -> true
        }
    }

    fun translateKnown(raw: String): String {
        val trimmed = raw.trim()
        return when {
            trimmed == "صاحب المنصة (عبدالرحمن)" || trimmed == "Platform Owner (Abdelrahman)" ->
                t("صاحب المنصة (عبدالرحمن)", "Platform Owner (Abdelrahman)")
            trimmed == "مطور معتمد" || trimmed == "Verified Developer" ->
                t("مطور معتمد", "Verified Developer")
            trimmed == "مطور تجريبي" || trimmed == "Beta Developer" ->
                t("مطور تجريبي", "Beta Developer")
            trimmed == "المطور المعتمد" ->
                t("المطور المعتمد", "Verified Developer")
            trimmed == "مطور" || trimmed == "Developer" ->
                t("مطور", "Developer")
            trimmed == "المالك الرسمي للمنصة" || trimmed == "Official Platform Owner" ->
                t("المالك الرسمي للمنصة", "Official Platform Owner")
            trimmed == "مختبر محترف" || trimmed == "Pro Tester" ->
                t("مختبر محترف", "Pro Tester")
            trimmed == "مختبر معتمد" || trimmed == "Verified Tester" ->
                t("مختبر معتمد", "Verified Tester")
            trimmed == "عضو مميز" || trimmed == "VIP Member" ->
                t("عضو مميز", "VIP Member")
            trimmed == "تطبيق قيد الاختبار المغلق لـ 14 يوماً" || trimmed == "App in 14-day closed testing track" ->
                t("تطبيق قيد الاختبار المغلق لـ 14 يوماً", "App in 14-day closed testing track")
            trimmed == "جلسة اختبار تفاعلية ناجحة وموثقة" || trimmed == "Verified interactive test session completed" ->
                t("جلسة اختبار تفاعلية ناجحة وموثقة", "Verified interactive test session completed")
            trimmed == "جلسة اختبار تفاعلية مكتملة بنجاح" || trimmed == "Interactive test session completed successfully" ->
                t("جلسة اختبار تفاعلية مكتملة بنجاح", "Interactive test session completed successfully")
            trimmed.startsWith("مختبر معتمد #") ->
                if (isArabic()) trimmed else trimmed.replace("مختبر معتمد #", "Verified Tester #")
            trimmed.startsWith("Verified Tester #") ->
                if (!isArabic()) trimmed else trimmed.replace("Verified Tester #", "مختبر معتمد #")
            trimmed.startsWith("مختبر #") ->
                if (isArabic()) trimmed else trimmed.replace("مختبر #", "Tester #")
            trimmed.startsWith("Tester #") ->
                if (!isArabic()) trimmed else trimmed.replace("Tester #", "مختبر #")
            trimmed.startsWith("جلسة فحص ") ->
                if (isArabic()) trimmed else trimmed.replace("جلسة فحص ", "Inspection Session ")
            else -> raw
        }
    }
}
