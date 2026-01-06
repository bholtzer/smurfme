package com.bih.applicationsmurfforyou.presentation.composeable.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// As a starting point, we'll use the built-in Cursive font family for a more playful feel.
// For a true Pixar look, you would download a font like "Luckiest Guy" from Google Fonts
// and create a custom FontFamily.
val CartoonFontFamily = FontFamily.Cursive

// Override the default typography with our new cartoon-style font
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = CartoonFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = CartoonFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CartoonFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CartoonFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp, // Increased size for readability
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CartoonFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
