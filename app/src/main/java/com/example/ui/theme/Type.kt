package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Professional modern Arabic & Latin Font Family (Cairo)
val AppFontFamily = FontFamily(
    Font(R.font.cairo, FontWeight.Normal),
    Font(R.font.cairo, FontWeight.Medium),
    Font(R.font.cairo, FontWeight.SemiBold),
    Font(R.font.cairo, FontWeight.Bold)
)

// Set of Material typography styles with Cairo font family
val Typography =
  Typography(
    displayLarge = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 28.sp,
      lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 22.sp,
      lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 18.sp,
      lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 15.sp,
      lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp,
      lineHeight = 19.sp
    ),
    bodyLarge = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 15.sp,
      lineHeight = 22.sp,
      letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 13.sp,
      lineHeight = 19.sp,
      letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 11.5.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 13.sp,
      lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 11.5.sp,
      lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
      fontFamily = AppFontFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 10.5.sp,
      lineHeight = 14.sp,
      letterSpacing = 0.3.sp
    )
  )

