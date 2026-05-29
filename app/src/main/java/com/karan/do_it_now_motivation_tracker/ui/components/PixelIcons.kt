package com.karan.do_it_now_motivation_tracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

// ── Core pixel-grid drawing engine ────────────────────────────────
// Each icon is defined as a 2D array: 0=transparent, 1=full color, 2=half-alpha
private fun DrawScope.drawPixelGrid(
    grid: Array<IntArray>,
    color: Color,
    pad: Float = 0.06f
) {
    val padX = size.width  * pad
    val padY = size.height * pad
    val cols = grid[0].size.toFloat()
    val rows = grid.size.toFloat()
    val cw   = (size.width  - padX * 2f) / cols
    val ch   = (size.height - padY * 2f) / rows
    val gap  = 0.8f   // tiny gap between pixels for pixel-art feel

    grid.forEachIndexed { r, row ->
        row.forEachIndexed { c, v ->
            if (v > 0) {
                drawRect(
                    color = when (v) {
                        2    -> color.copy(alpha = 0.45f)
                        else -> color
                    },
                    topLeft = Offset(padX + c * cw + gap, padY + r * ch + gap),
                    size    = Size(cw - gap * 2f, ch - gap * 2f)
                )
            }
        }
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//   NAVIGATION BAR ICONS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

private val homeGrid = arrayOf(
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,1,1,1,1,0,0),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(0,1,1,0,0,1,1,0),
    intArrayOf(0,1,1,0,0,1,1,0),
    intArrayOf(0,1,1,1,1,1,1,0)
)

@Composable
fun PixelIconHome(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(homeGrid, color) }
}

// ─────────────────────────────────────────────────────────────────

private val clipboardGrid = arrayOf(
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(1,1,0,1,1,0,1,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,1,1,1,1,0,1),
    intArrayOf(1,0,1,1,1,1,0,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,0,1,1,1,1,0,1),
    intArrayOf(1,1,1,1,1,1,1,1)
)

@Composable
fun PixelIconClipboard(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(clipboardGrid, color) }
}

// ─────────────────────────────────────────────────────────────────

private val barChartNavGrid = arrayOf(
    intArrayOf(0,0,0,0,0,0,1,0),
    intArrayOf(0,0,0,0,0,0,1,0),
    intArrayOf(0,0,0,0,1,0,1,0),
    intArrayOf(0,0,0,0,1,0,1,0),
    intArrayOf(0,1,0,0,1,0,1,0),
    intArrayOf(0,1,0,0,1,0,1,0),
    intArrayOf(0,1,0,0,1,0,1,0),
    intArrayOf(1,1,1,1,1,1,1,1)
)

@Composable
fun PixelIconBarChartNav(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(barChartNavGrid, color) }
}

// ─────────────────────────────────────────────────────────────────

private val gearGrid = arrayOf(
    intArrayOf(0,0,1,1,1,1,0,0),
    intArrayOf(0,1,0,1,1,0,1,0),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,1,0,0,0,0,1,1),
    intArrayOf(1,1,0,0,0,0,1,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(0,1,0,1,1,0,1,0),
    intArrayOf(0,0,1,1,1,1,0,0)
)

@Composable
fun PixelIconGear(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(gearGrid, color) }
}

// ─────────────────────────────────────────────────────────────────

private val plusGrid = arrayOf(
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0)
)

@Composable
fun PixelIconPlus(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(plusGrid, color) }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//   DASHBOARD STAT ICONS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

private val flameGrid = arrayOf(
    intArrayOf(0,0,0,0,1,0,0,0),
    intArrayOf(0,0,0,1,1,1,0,0),
    intArrayOf(0,0,1,1,1,1,1,0),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(0,1,1,1,1,1,0,0),
    intArrayOf(0,0,1,1,1,0,0,0),
    intArrayOf(0,0,0,1,0,0,0,0)
)

@Composable
fun PixelIconFlame(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(flameGrid, color) }
}

// ─────────────────────────────────────────────────────────────────

private val lineChartGrid = arrayOf(
    intArrayOf(0,0,0,0,0,0,1,0),
    intArrayOf(0,0,0,0,0,1,1,1),
    intArrayOf(0,0,0,0,0,0,1,0),
    intArrayOf(0,0,0,1,0,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,1,0,0,1,0,0,0),
    intArrayOf(0,1,0,0,0,0,0,0),
    intArrayOf(1,1,1,1,1,1,1,1)
)

@Composable
fun PixelIconLineChart(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(lineChartGrid, color) }
}

// ─────────────────────────────────────────────────────────────────

private val calendarGrid = arrayOf(
    intArrayOf(0,1,0,0,0,0,1,0),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,1,0,1,0,1,0),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,1,0,1,0,1,0),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,1,0,1,0,1,0)
)

@Composable
fun PixelIconCalendar(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(calendarGrid, color) }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//   DIFFICULTY ICONS  (used in Add/Edit/Onboarding)
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// Easy — Thumbs Up
private val thumbsUpGrid = arrayOf(
    intArrayOf(0,0,1,0,0,0,0,0),
    intArrayOf(0,0,1,0,0,0,0,0),
    intArrayOf(0,0,1,1,1,1,1,0),
    intArrayOf(1,0,1,1,1,1,1,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(0,1,1,1,1,1,1,0)
)

@Composable
fun PixelIconThumbsUp(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(thumbsUpGrid, color) }
}

// Medium — Rising bar chart
private val risingBarsGrid = arrayOf(
    intArrayOf(0,0,0,0,0,0,1,0),
    intArrayOf(0,0,0,0,0,0,1,0),
    intArrayOf(0,0,0,0,1,0,1,0),
    intArrayOf(0,0,1,0,1,0,1,0),
    intArrayOf(0,0,1,0,1,0,1,0),
    intArrayOf(0,0,1,0,1,0,1,0),
    intArrayOf(0,0,1,0,1,0,1,0),
    intArrayOf(1,1,1,1,1,1,1,1)
)

@Composable
fun PixelIconRisingBars(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(risingBarsGrid, color) }
}

// Hard — 3 Candles with wicks
private val candlesGrid = arrayOf(
    intArrayOf(0,1,0,0,1,0,1,0),
    intArrayOf(0,1,0,0,1,0,1,0),
    intArrayOf(1,1,0,1,1,1,1,1),
    intArrayOf(1,1,0,1,1,1,1,1),
    intArrayOf(1,1,0,1,1,1,1,1),
    intArrayOf(1,1,0,1,1,1,1,1),
    intArrayOf(1,1,0,1,1,1,1,1),
    intArrayOf(1,1,0,1,1,1,1,1)
)

@Composable
fun PixelIconCandles(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(candlesGrid, color) }
}

// Boss — Bull Head with horns
private val bullGrid = arrayOf(
    intArrayOf(1,1,0,0,0,0,1,1),
    intArrayOf(1,1,0,0,0,0,1,1),
    intArrayOf(0,1,1,0,0,1,1,0),
    intArrayOf(0,0,1,1,1,1,0,0),
    intArrayOf(0,0,1,0,0,1,0,0),
    intArrayOf(0,0,1,1,1,1,0,0),
    intArrayOf(0,0,1,0,0,1,0,0),
    intArrayOf(0,0,1,0,0,1,0,0)
)

@Composable
fun PixelIconBull(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(bullGrid, color) }
}

/** Dispatcher: returns correct difficulty icon as composable */
@Composable
fun PixelDifficultyIcon(difficulty: String, modifier: Modifier = Modifier, color: Color = Color.White) {
    when (difficulty.lowercase()) {
        "easy"   -> PixelIconThumbsUp(modifier, color)
        "medium" -> PixelIconRisingBars(modifier, color)
        "hard"   -> PixelIconCandles(modifier, color)
        "boss"   -> PixelIconBull(modifier, color)
        else     -> PixelIconThumbsUp(modifier, color)
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//   CATEGORY ICONS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// MIND — Brain silhouette in head outline
private val brainGrid = arrayOf(
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(1,0,1,0,1,0,0,1),
    intArrayOf(1,1,0,1,0,1,0,1),
    intArrayOf(1,0,1,0,1,0,1,1),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,0,0,0,0,0)
)

@Composable
fun PixelIconBrain(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(brainGrid, color) }
}

// BODY — Stick figure lifting a barbell
private val weightlifterGrid = arrayOf(
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,0,0,0,0,0),
    intArrayOf(1,0,1,1,1,1,0,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,1,1,1,1,0,1),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,1,0,0,1,0,0)
)

@Composable
fun PixelIconWeightlifter(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(weightlifterGrid, color) }
}

// CODE — Terminal window with >_
private val terminalGrid = arrayOf(
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,1,0,0,1,0,0,1),
    intArrayOf(1,0,1,0,0,0,0,1),
    intArrayOf(1,1,0,0,1,1,0,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,1,1,1,1,1,1,1)
)

@Composable
fun PixelIconTerminal(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(terminalGrid, color) }
}

// WORK — Briefcase
private val briefcaseGrid = arrayOf(
    intArrayOf(0,0,1,1,1,1,0,0),
    intArrayOf(0,0,1,0,0,1,0,0),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,1,1,1,1,1,1,1)
)

@Composable
fun PixelIconBriefcase(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(briefcaseGrid, color) }
}

// FINANCE — Coin with $ line
private val coinGrid = arrayOf(
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(1,0,0,1,1,0,0,1),
    intArrayOf(1,0,0,1,1,0,0,1),
    intArrayOf(1,1,0,1,1,0,1,1),
    intArrayOf(1,0,0,1,1,0,0,1),
    intArrayOf(1,0,0,1,1,0,0,1),
    intArrayOf(1,0,0,1,1,0,0,1),
    intArrayOf(0,1,1,1,1,1,1,0)
)

@Composable
fun PixelIconCoin(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(coinGrid, color) }
}

// CREATE — Palette with dot dabs
private val paletteGrid = arrayOf(
    intArrayOf(0,1,1,1,1,1,0,0),
    intArrayOf(1,0,0,0,0,1,1,0),
    intArrayOf(1,1,0,0,0,0,1,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,0,1,0,1,0,0,1),
    intArrayOf(0,1,0,0,0,1,1,0),
    intArrayOf(0,0,1,1,1,0,0,0),
    intArrayOf(0,0,0,0,0,0,0,0)
)

@Composable
fun PixelIconPalette(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(paletteGrid, color) }
}

// SPIRIT — Leaf shape
private val leafGrid = arrayOf(
    intArrayOf(0,0,0,0,1,1,0,0),
    intArrayOf(0,0,0,1,1,1,1,0),
    intArrayOf(0,0,1,1,1,1,1,0),
    intArrayOf(0,1,1,1,1,1,0,0),
    intArrayOf(0,1,1,1,1,0,0,0),
    intArrayOf(0,0,1,1,0,0,0,0),
    intArrayOf(0,0,0,1,0,0,0,0),
    intArrayOf(0,0,0,1,0,0,0,0)
)

@Composable
fun PixelIconLeaf(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(leafGrid, color) }
}

// MISSION — Crosshair / Target
private val targetGrid = arrayOf(
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,1,0,0,1,0,0),
    intArrayOf(1,1,0,0,0,0,1,1),
    intArrayOf(1,1,0,0,0,0,1,1),
    intArrayOf(0,0,1,0,0,1,0,0),
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,0,0,1,1,0,0,0)
)

@Composable
fun PixelIconTarget(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(targetGrid, color) }
}

/** Dispatcher: returns correct category icon as composable */
@Composable
fun PixelCategoryIcon(category: String, modifier: Modifier = Modifier, color: Color = Color.White) {
    when (category) {
        "Mind"    -> PixelIconBrain(modifier, color)
        "Body"    -> PixelIconWeightlifter(modifier, color)
        "Code"    -> PixelIconTerminal(modifier, color)
        "Work"    -> PixelIconBriefcase(modifier, color)
        "Finance" -> PixelIconCoin(modifier, color)
        "Create"  -> PixelIconPalette(modifier, color)
        "Spirit"  -> PixelIconLeaf(modifier, color)
        "Mission" -> PixelIconTarget(modifier, color)
        // Legacy fallbacks
        "Health", "Fitness" -> PixelIconWeightlifter(modifier, color)
        "Study"  -> PixelIconBrain(modifier, color)
        else     -> PixelIconTarget(modifier, color)
    }
}

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//   BADGE ICONS
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

// Shield with checkmark — Disciplined
private val shieldGrid = arrayOf(
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(1,1,0,0,0,0,1,1),
    intArrayOf(1,0,0,0,1,0,0,1),
    intArrayOf(1,0,0,1,0,0,0,1),
    intArrayOf(1,0,1,0,0,0,0,1),
    intArrayOf(0,1,0,0,0,0,1,0),
    intArrayOf(0,0,1,0,0,1,0,0),
    intArrayOf(0,0,0,1,1,0,0,0)
)

@Composable
fun PixelIconShield(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(shieldGrid, color) }
}

// Stopwatch — Consistent
private val stopwatchGrid = arrayOf(
    intArrayOf(0,0,0,1,1,0,0,0),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(1,1,0,1,0,0,1,1),
    intArrayOf(1,0,0,1,0,0,0,1),
    intArrayOf(1,0,0,1,1,0,0,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(0,1,1,1,1,1,1,0),
    intArrayOf(0,0,0,1,0,1,0,0)
)

@Composable
fun PixelIconStopwatch(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(stopwatchGrid, color) }
}

// Lightning bolt — Achiever
private val lightningGrid = arrayOf(
    intArrayOf(0,0,1,1,1,1,0,0),
    intArrayOf(0,0,1,1,1,0,0,0),
    intArrayOf(0,0,1,1,0,0,0,0),
    intArrayOf(1,1,1,1,1,1,0,0),
    intArrayOf(0,0,0,1,1,1,1,1),
    intArrayOf(0,0,0,0,0,1,1,0),
    intArrayOf(0,0,0,0,0,0,1,0),
    intArrayOf(0,0,0,0,0,0,0,0)
)

@Composable
fun PixelIconLightning(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(lightningGrid, color) }
}

// Crown — Legend
private val crownGrid = arrayOf(
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,0,1,0,0,1,0,1),
    intArrayOf(1,1,1,0,0,1,1,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,1,1,1,1,1,1,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,0,0,0,0,0,0,1),
    intArrayOf(1,1,1,1,1,1,1,1)
)

@Composable
fun PixelIconCrown(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(crownGrid, color) }
}

// Trophy — On Fire
private val trophyGrid = arrayOf(
    intArrayOf(1,1,1,1,1,1,1,0),
    intArrayOf(1,0,1,1,1,0,1,0),
    intArrayOf(1,0,0,1,0,0,1,0),
    intArrayOf(0,1,1,1,1,1,0,0),
    intArrayOf(0,0,1,1,1,0,0,0),
    intArrayOf(0,0,0,1,0,0,0,0),
    intArrayOf(0,1,1,1,1,1,0,0),
    intArrayOf(1,1,1,1,1,1,1,0)
)

@Composable
fun PixelIconTrophy(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) { drawPixelGrid(trophyGrid, color) }
}
