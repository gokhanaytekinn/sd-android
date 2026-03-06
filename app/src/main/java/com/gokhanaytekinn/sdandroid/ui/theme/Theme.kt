package com.gokhanaytekinn.sdandroid.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = AccentColor,
    tertiary = WarningColor,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = Color(0xFFF1F5F9), // slate-100
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextWhite,
    onBackground = TextPrimary,         // slate-900
    onSurface = TextPrimary,            // slate-900
    onSurfaceVariant = TextSecondary,   // slate-500
    error = ErrorColor,
    outline = Color(0xFFE2E8F0),        // slate-200
    outlineVariant = Color(0xFFCBD5E1)  // slate-300
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = AccentColor,
    tertiary = WarningColor,
    background = BackgroundDark,
    surface = SurfaceDark,              // #1E293B
    surfaceVariant = Color(0xFF273549), // slightly lighter than surface
    onPrimary = BackgroundDark,
    onSecondary = TextWhite,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onSurfaceVariant = TextGray,        // slate-400
    error = ErrorColor,
    outline = Color(0xFF334155),        // slate-700
    outlineVariant = Color(0xFF475569)  // slate-600
)

@Composable
fun SDAndroidTheme(
    darkTheme: Boolean = true, // Dark theme as default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val typography = androidx.compose.runtime.remember(configuration.screenWidthDp) {
        getTypography(configuration.screenWidthDp)
    }

    CompositionLocalProvider(LocalIndication provides NoIndication) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}

private object NoIndication : Indication {
    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return NoIndicationInstance
    }
}

private object NoIndicationInstance : IndicationInstance {
    override fun ContentDrawScope.drawIndication() {
        drawContent()
    }
}
