package com.nader.riyadalsalheen.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.nader.riyadalsalheen.R

// Set of Material typography styles to start with
val AmiriFontFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri_bold, FontWeight.Bold)
)

val CairoFontFamily = FontFamily(
    Font(R.font.cairo, FontWeight.Normal),
    Font(R.font.cairo_semibold, FontWeight.SemiBold),
    Font(R.font.cairo_bold, FontWeight.Bold)
)

// Default Material 3 typography values
val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = CairoFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = CairoFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = CairoFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = CairoFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = CairoFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = CairoFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = CairoFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = CairoFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = CairoFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = AmiriFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = CairoFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = CairoFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = CairoFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = CairoFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = CairoFontFamily),
)

val Typography2 = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = FontFamily.Default),
    displayMedium = baseline.displayMedium.copy(fontFamily = FontFamily.Default),
    displaySmall = baseline.displaySmall.copy(fontFamily = FontFamily.Default),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = FontFamily.Default),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = FontFamily.Default),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = FontFamily.Default),
    titleLarge = baseline.titleLarge.copy(fontFamily = FontFamily.Default),
    titleMedium = baseline.titleMedium.copy(fontFamily = FontFamily.Default),
    titleSmall = baseline.titleSmall.copy(fontFamily = FontFamily.Default),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = FontFamily.Default),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = FontFamily.Default),
    bodySmall = baseline.bodySmall.copy(fontFamily = FontFamily.Default),
    labelLarge = baseline.labelLarge.copy(fontFamily = FontFamily.Default),
    labelMedium = baseline.labelMedium.copy(fontFamily = FontFamily.Default),
    labelSmall = baseline.labelSmall.copy(fontFamily = FontFamily.Default),
)