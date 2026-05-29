package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import kotlinx.coroutines.delay

// ── Pixel hourglass grid (17 wide × 19 tall) ─────────────────
private val hourglassFrame = setOf(
    // Top bar
    2..14 to 0, 1..15 to 1,
    // Top slopes
    2..3 to 2, 13..14 to 2,
    3..4 to 3, 12..13 to 3,
    4..5 to 4, 11..12 to 4,
    5..6 to 5, 10..11 to 5,
    6..7 to 6, 9..10 to 6,
    7..8 to 7, 8..9 to 7,
    // Neck
    8..8 to 8,
    7..9 to 9,
    // Bottom slopes
    6..7 to 10, 9..10 to 10,
    5..6 to 11, 10..11 to 11,
    4..5 to 12, 11..12 to 12,
    3..4 to 13, 12..13 to 13,
    2..3 to 14, 13..14 to 14,
    1..15 to 15,
    // Bottom bar
    2..14 to 16, 1..15 to 17,
    3..13 to 18
).flatMap { (xRange, y) ->
    xRange.map { x -> x to y }
}.toSet()

private fun isFramePixel(x: Int, y: Int): Boolean {
    return hourglassFrame.contains(x to y)
}

// Sand fills top compartment rows 2-7 (full rows inside frame)
private fun isTopSand(x: Int, y: Int, progress: Float): Boolean {
    if (y !in 2..7) return false
    val fullRows = 6 // rows 2–7
    val sandRows = (fullRows * (1f - progress)).toInt()
    if (y > 2 + sandRows - 1) return false
    val innerStart = when (y) { 2 -> 4; 3 -> 5; 4 -> 6; 5 -> 7; 6 -> 8; else -> 8 }
    val innerEnd   = when (y) { 2 -> 13; 3 -> 12; 4 -> 11; 5 -> 10; 6 -> 9; else -> 9 }
    return x in innerStart..innerEnd
}

// Sand fills bottom compartment rows 10-16
private fun isBottomSand(x: Int, y: Int, progress: Float): Boolean {
    if (y !in 10..16) return false
    val fullRows = 7
    val sandRows = (fullRows * progress).toInt()
    val bottomRow = 16
    if (y < bottomRow - sandRows + 1) return false
    val innerStart = when (y) { 10 -> 8; 11 -> 7; 12 -> 6; 13 -> 5; 14 -> 4; 15 -> 3; else -> 3 }
    val innerEnd   = when (y) { 10 -> 9; 11 -> 10; 12 -> 11; 13 -> 12; 14 -> 13; 15 -> 14; else -> 14 }
    return x in innerStart..innerEnd
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val sandProgress = remember { Animatable(0f) }
    val textAlpha    = remember { Animatable(0f) }

    val inf = rememberInfiniteTransition(label = "blink")
    val cursorBlink by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse),
        label = "blink"
    )

    LaunchedEffect(Unit) {
        delay(200)
        textAlpha.animateTo(1f, tween(600))
        sandProgress.animateTo(1f, tween(2400, easing = LinearEasing))
        delay(600)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(60.dp))

            // ── App Title ──────────────────────────────────────
            Column(
                modifier = Modifier.alpha(textAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text      = "DO IT",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize  = 36.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text      = "NOW",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize  = 36.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )
            }

            // ── Pixel Hourglass ────────────────────────────────
            val hourglassColor = MaterialTheme.colorScheme.onBackground
            Canvas(modifier = Modifier.size(200.dp, 240.dp)) {
                val cols = 17
                val rows = 19
                val cellW = size.width / cols
                val cellH = size.height / rows
                val progress = sandProgress.value

                for (row in 0 until rows) {
                    for (col in 0 until cols) {
                        val draw = when {
                            isFramePixel(col, row)                    -> true
                            isBottomSand(col, row, progress)          -> true
                            isTopSand(col, row, progress)             -> true
                            // Falling sand stream at neck
                            col == 8 && row == 8 && progress < 0.9f  -> true
                            else                                       -> false
                        }
                        if (draw) {
                            drawRoundRect(
                                color = hourglassColor,
                                topLeft = Offset(col * cellW + 1f, row * cellH + 1f),
                                size = Size(cellW - 2f, cellH - 2f),
                                cornerRadius = CornerRadius(1f)
                            )
                        }
                    }
                }
            }

            // ── Subtitle ───────────────────────────────────────
            Column(
                modifier = Modifier.alpha(textAlpha.value).padding(bottom = 72.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text      = "MAXIMIZE YOUR",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize  = 12.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text      = "PRODUCTIVITY",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize  = 12.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
