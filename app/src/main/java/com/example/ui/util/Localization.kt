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
}
