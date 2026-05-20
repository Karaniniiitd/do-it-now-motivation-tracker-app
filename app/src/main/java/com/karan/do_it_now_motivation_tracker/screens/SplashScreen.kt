package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.ui.theme.AtmosphericBlack
import com.karan.do_it_now_motivation_tracker.ui.theme.CyanGlow
import com.karan.do_it_now_motivation_tracker.ui.theme.MutedText
import com.karan.do_it_now_motivation_tracker.ui.theme.SandPixel
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftIndigo
import kotlinx.coroutines.delay

// Pixel art hourglass frame (17 wide x 19 tall)
// '#' = frame pixel, '.' = empty
private val hourglassFrame = listOf(
    "#################",
    "#################",
    "##.............##",
    ".##...........##.",
    "..##.........##..",
    "...##.......##...",
    "....##.....##....",
    ".....##...##.....",
    "......##.##......",
    ".......###.......",
    "......##.##......",
    ".....##...##.....",
    "....##.....##....",
    "...##.......##...",
    "..##.........##..",
    ".##...........##.",
    "##.............##",
    "#################",
    "#################"
)

// Interior sand rows for top half (rows 2-8) - (startCol, endCol) inclusive
private val topSandRows = listOf(
    2 to Pair(2, 14),   // row 2: 13 pixels
    3 to Pair(3, 13),   // row 3: 11
    4 to Pair(4, 12),   // row 4: 9
    5 to Pair(5, 11),   // row 5: 7
    6 to Pair(6, 10),   // row 6: 5
    7 to Pair(7, 9),    // row 7: 3
    8 to Pair(8, 8),    // row 8: 1
)

// Interior sand rows for bottom half (rows 10-16) - mirror
private val bottomSandRows = listOf(
    10 to Pair(8, 8),
    11 to Pair(7, 9),
    12 to Pair(6, 10),
    13 to Pair(5, 11),
    14 to Pair(4, 12),
    15 to Pair(3, 13),
    16 to Pair(2, 14),
)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val sandProgress = remember { Animatable(0f) }

    val inf = rememberInfiniteTransition(label = "splash")
    val glowPulse by inf.animateFloat(
        initialValue = 0.15f, targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = LinearEasing), RepeatMode.Reverse
        ), label = "glow"
    )

    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(300)
        textAlpha.animateTo(1f, tween(800))
        sandProgress.animateTo(1f, tween(2200, easing = FastOutSlowInEasing))
        delay(500)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(AtmosphericBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Ambient glow behind hourglass
                Canvas(modifier = Modifier.size(220.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                CyanGlow.copy(alpha = glowPulse * 0.2f),
                                SoftIndigo.copy(alpha = glowPulse * 0.08f),
                                Color.Transparent
                            ), radius = size.minDimension * 0.6f
                        ), radius = size.minDimension * 0.6f
                    )
                }

                // Pixel art hourglass
                Canvas(modifier = Modifier.size(170.dp)) {
                    val cols = 17
                    val rows = 19
                    val pixelW = size.width / cols
                    val pixelH = size.height / rows
                    val pSize = Size(pixelW - 1f, pixelH - 1f) // 1px gap for pixel look

                    val prog = sandProgress.value
                    val topVisible = ((1f - prog) * 7).toInt() // 0..7 rows remaining
                    val bottomVisible = (prog * 7).toInt()     // 0..7 rows filled

                    // Draw frame
                    for (r in 0 until rows) {
                        val line = hourglassFrame[r]
                        for (c in 0 until cols) {
                            if (line[c] == '#') {
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(c * pixelW, r * pixelH),
                                    size = pSize
                                )
                            }
                        }
                    }

                    // Draw top sand (empties from bottom)
                    for (i in 0 until topVisible) {
                        val (row, range) = topSandRows[i]
                        val (sc, ec) = range
                        for (c in sc..ec) {
                            drawRect(
                                color = SandPixel,
                                topLeft = Offset(c * pixelW, row * pixelH),
                                size = pSize
                            )
                        }
                    }

                    // Draw bottom sand (fills from bottom)
                    for (i in 0 until bottomVisible) {
                        val idx = bottomSandRows.size - 1 - i // fill from bottom row up
                        val (row, range) = bottomSandRows[idx]
                        val (sc, ec) = range
                        for (c in sc..ec) {
                            drawRect(
                                color = SandPixel,
                                topLeft = Offset(c * pixelW, row * pixelH),
                                size = pSize
                            )
                        }
                    }

                    // Falling stream through neck
                    if (prog in 0.05f..0.92f) {
                        val neckCol = 8
                        drawRect(
                            color = SandPixel.copy(alpha = 0.8f),
                            topLeft = Offset(neckCol * pixelW, 9 * pixelH),
                            size = pSize
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Do It Now",
                fontSize = 28.sp,
                fontWeight = FontWeight.Thin,
                color = Color.White.copy(alpha = textAlpha.value),
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "build discipline",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText.copy(alpha = textAlpha.value * 0.7f),
                letterSpacing = 3.sp
            )
        }
    }
}
