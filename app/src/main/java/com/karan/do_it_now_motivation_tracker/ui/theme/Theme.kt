package com.karan.do_it_now_motivation_tracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CinematicDark = darkColorScheme(
    primary = CyanGlow,
    onPrimary = AtmosphericBlack,
    primaryContainer = SoftIndigo,
    secondary = MutedPurple,
    onSecondary = SoftWhite,
    tertiary = WarmAmber,
    background = AtmosphericBlack,
    onBackground = SoftWhite,
    surface = DarkBlueSurface,
    onSurface = SoftWhite,
    surfaceVariant = MidnightCard,
    onSurfaceVariant = MutedText,
    error = SoftRed,
    onError = SoftWhite,
    outline = CardBorder
)

@Composable
fun Do_It_Now_Motivation_TrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CinematicDark,
        typography = Typography,
        content = content
    )
}