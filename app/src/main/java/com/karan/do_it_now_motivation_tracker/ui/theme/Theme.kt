package com.karan.do_it_now_motivation_tracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkPixelColorScheme = darkColorScheme(
    primary          = Color.White,
    onPrimary        = Color.Black,
    secondary        = Color(0xFFCCCCCC),
    onSecondary      = Color.Black,
    background       = Color.Black,
    onBackground     = Color.White,
    surface          = Color(0xFF111111),
    onSurface        = Color.White,
    surfaceVariant   = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline          = Color(0xFF444444),
    error            = Color(0xFFCCCCCC),
    onError          = Color.Black
)

private val LightPixelColorScheme = lightColorScheme(
    primary          = Color.Black,
    onPrimary        = Color.White,
    secondary        = Color(0xFF333333),
    onSecondary      = Color.White,
    background       = Color.White,
    onBackground     = Color.Black,
    surface          = Color(0xFFEEEEEE),
    onSurface        = Color.Black,
    surfaceVariant   = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF555555),
    outline          = Color(0xFFCCCCCC),
    error            = Color(0xFF333333),
    onError          = Color.White
)

@Composable
fun Do_It_Now_Motivation_TrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkPixelColorScheme else LightPixelColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}