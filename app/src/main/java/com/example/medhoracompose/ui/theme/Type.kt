package com.example.medhoracompose.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun text(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = FontFamily.SansSerif, fontSize = size.sp, lineHeight = TextUnit.Unspecified,
    platformStyle = PlatformTextStyle(includeFontPadding = true),
    fontWeight = weight, letterSpacing = 0.sp
)

val Typography = Typography(
    titleLarge = text(22, 28, FontWeight.Bold),
    titleMedium = text(15, 20, FontWeight.Bold),
    titleSmall = text(15, 20, FontWeight.Bold),
    bodyLarge = text(16, 24),
    bodyMedium = text(14, 20),
    bodySmall = text(12, 16),
    labelLarge = text(14, 20, FontWeight.Medium),
    labelMedium = text(12, 16, FontWeight.Medium),
    labelSmall = text(10, 14, FontWeight.Medium)
)
