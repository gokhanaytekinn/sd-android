package com.gokhanaytekinn.sdandroid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun dpToSp(dp: Int, screenWidthDp: Int): androidx.compose.ui.unit.TextUnit {
    val baselineWidth = 360 // Standard small phone baseline
    val scale = screenWidthDp.toFloat() / baselineWidth
    // Clamp scale between 0.85 and 1.2
    val clampedScale = scale.coerceIn(0.85f, 1.2f)
    return (dp * clampedScale).sp
}

fun getTypography(screenWidthDp: Int) = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = dpToSp(16, screenWidthDp),
        lineHeight = dpToSp(24, screenWidthDp),
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = dpToSp(22, screenWidthDp),
        lineHeight = dpToSp(28, screenWidthDp),
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = dpToSp(11, screenWidthDp),
        lineHeight = dpToSp(16, screenWidthDp),
        letterSpacing = 0.5.sp
    )
)

val Typography = getTypography(360) // Keep original for compatibility if needed elsewhere
