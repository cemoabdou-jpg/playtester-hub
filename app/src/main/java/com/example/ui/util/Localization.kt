package com.example.ui.util

import androidx.compose.runtime.*

enum class AppLanguage {
    ARABIC, ENGLISH
}

object L {
    var currentLanguage by mutableStateOf(AppLanguage.ARABIC)

    fun isArabic(): Boolean = currentLanguage == AppLanguage.ARABIC
}
