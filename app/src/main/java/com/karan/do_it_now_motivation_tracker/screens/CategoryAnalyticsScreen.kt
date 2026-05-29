package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.ui.components.Categories
import com.karan.do_it_now_motivation_tracker.ui.components.PixelCategoryIcon
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CategoryAnalyticsScreen(viewModel: GoalViewModel) {
    val activeGoals    by viewModel.activeGoals.collectAsState()
    val completedGoals by viewModel.completedGoals.collectAsState()
    val currentStreak  by viewModel.currentStreak.collectAsState()
    val userStats      by viewModel.userStats.collectAsState()
    val failedGoals    by viewModel.failedGoals.collectAsState()

    val allGoals = activeGoals + completedGoals + failedGoals

    // Per-category breakdown
    data class CatStat(
        val name: String,
        val total: Int,
        val done: Int,
        val failed: Int,
        val rate: Float
    )

    val catStats = remember(allGoals) {
        Categories.all.map { cat ->
            val total = allGoals.count { it.category == cat }
            val done  = allGoals.count { it.category == cat && it.isCompleted }
            val failed = allGoals.count { it.category == cat && it.isFailed }
            CatStat(cat, total, done, failed, if (total > 0) done.toFloat() / total else 0f)
        }.sortedByDescending { it.total }
    }

    val totalGoals    = allGoals.size
    val totalCompleted = completedGoals.size

    // Colors per category (all white palette, different alpha for pie slices)
    val sliceAlphas = listOf(1f, 0.85f, 0.70f, 0.55f, 0.42f, 0.30f, 0.20f, 0.12f)

    // Pie chart data
    val pieData = remember(catStats) {
        catStats.filter { it.total > 0 }.take(8)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(44.dp))

        Text(
            "ANALYTICS",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize  = 26.sp,
            fontFamily = PixelFontFamily,
            letterSpacing = 2.sp
        )

        // ── Top-level stats ───────────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnalyticStatBox(Modifier.weight(1f), label = "TOTAL\nMISSIONS", value = "$totalGoals")
            AnalyticStatBox(Modifier.weight(1f), label = "COMPLETED", value = "$totalCompleted")
            AnalyticStatBox(
                Modifier.weight(1f),
                label = "FAILED",
                value = "${failedGoals.size}",
                valueColor = Color(0xFFFF4444)
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnalyticStatBox(
                Modifier.weight(1f),
                label = "COMPLETION\nRATE",
                value = if (totalGoals > 0) "${(totalCompleted * 100 / totalGoals)}%" else "0%"
            )
            AnalyticStatBox(Modifier.weight(1f), label = "STREAK\nDAYS", value = "$currentStreak")
            AnalyticStatBox(Modifier.weight(1f), label = "LEVEL", value = "LV ${userStats.level}")
            AnalyticStatBox(Modifier.weight(1f), label = "TOTAL XP", value = "${userStats.totalXp}")
        }

        // ── Pixel Pie Chart ───────────────────────────────────────
        if (pieData.isNotEmpty()) {
            Text(
                "MISSIONS BY CATEGORY",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize  = 11.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 1.sp
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                val total = pieData.sumOf { it.total }.toFloat().coerceAtLeast(1f)
                val pieSize = 200.dp
                val arcColor = MaterialTheme.colorScheme.onBackground

                Canvas(Modifier.size(pieSize)) {
                    var startAngle = -90f
                    pieData.forEachIndexed { i, cat ->
                        val sweep  = (cat.total / total) * 360f
                        val alpha  = sliceAlphas.getOrElse(i) { 0.1f }
                        val stroke = 40.dp.toPx()
                        val r      = (size.minDimension - stroke) / 2f
                        drawArc(
                            color = arcColor.copy(alpha = alpha),
                            startAngle = startAngle,
                            sweepAngle = sweep - 2f,        // 2° gap between slices
                            useCenter  = false,
                            topLeft    = Offset(center.x - r, center.y - r),
                            size       = Size(r * 2, r * 2),
                            style      = Stroke(stroke, cap = StrokeCap.Square)
                        )
                        startAngle += sweep
                    }
                }
            }

            // ── Pie legend ─────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pieData.forEachIndexed { i, cat ->
                    val alpha = sliceAlphas.getOrElse(i) { 0.1f }
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Color swatch
                        Box(
                            Modifier
                                .size(14.dp)
                                .background(Color.White.copy(alpha = alpha))
                                .border(1.dp, Color.White.copy(alpha = alpha))
                        )
                        PixelCategoryIcon(
                            category = cat.name,
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
                        )
                        Text(
                            cat.name.uppercase(),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha),
                            fontSize   = 8.sp,
                            fontFamily = PixelFontFamily,
                            modifier   = Modifier.weight(1f)
                        )
                        Text(
                            "${cat.total} missions",
                            color      = Color(0xFF888888),
                            fontSize   = 8.sp,
                            fontFamily = PixelFontFamily
                        )
                    }
                }
            }
        }

        // ── Per-category breakdown ────────────────────────────────
        Text(
            "COMPLETION RATE PER CATEGORY",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize  = 11.sp,
            fontFamily = PixelFontFamily,
            letterSpacing = 1.sp
        )

        catStats.forEach { cat ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(if (cat.total > 0) 2.dp else 1.dp, if (cat.total > 0) Color.White else Color(0xFF2A2A2A))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PixelCategoryIcon(
                                category = cat.name,
                                modifier = Modifier.size(18.dp),
                                color    = if (cat.total > 0) Color.White else Color(0xFF333333)
                            )
                            Text(
                                cat.name.uppercase(),
                                color      = if (cat.total > 0) Color.White else Color(0xFF333333),
                                fontSize   = 9.sp,
                                fontFamily = PixelFontFamily
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                if (cat.total > 0) "${cat.done}/${cat.total} DONE" else "NO MISSIONS",
                                color      = Color(0xFF888888),
                                fontSize   = 8.sp,
                                fontFamily = PixelFontFamily
                            )
                            if (cat.failed > 0) {
                                Text(
                                    "${cat.failed} FAILED",
                                    color      = Color(0xFFFF4444),
                                    fontSize   = 8.sp,
                                    fontFamily = PixelFontFamily
                                )
                            }
                        }
                    }

                    if (cat.total > 0) {
                        // 10-segment completion bar
                        val segments = 10
                        val filled   = (cat.rate * segments).toInt()
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            repeat(segments) { i ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .background(if (i < filled) Color.White else MaterialTheme.colorScheme.surface)
                                )
                            }
                        }

                        // Suggestion text
                        val suggestion = when {
                            cat.rate == 0f  -> "NO COMPLETIONS YET. START HERE."
                            cat.rate < 0.3f -> "NEEDS ATTENTION. PRIORITIZE THIS."
                            cat.rate < 0.7f -> "SOLID PROGRESS. KEEP GOING."
                            else            -> "CRUSHING IT. MAINTAIN THIS."
                        }
                        Text(
                            suggestion,
                            color      = when {
                                cat.rate < 0.3f -> Color(0xFF888888)
                                cat.rate < 0.7f -> Color(0xFFAAAAAA)
                                else            -> Color(0xFFCCCCCC)
                            },
                            fontSize   = 7.sp,
                            fontFamily = PixelFontFamily
                        )
                    }
                }
            }
        }

        // ── Weak areas warning ────────────────────────────────────
        val neglected = catStats.filter { it.total == 0 }
        if (neglected.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF444444))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "UNTOUCHED AREAS",
                        color      = Color(0xFF888888),
                        fontSize   = 9.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 1.sp
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        neglected.forEach { cat ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                PixelCategoryIcon(
                                    category = cat.name,
                                    modifier = Modifier.size(22.dp),
                                    color    = Color(0xFF444444)
                                )
                                Text(
                                    cat.name.uppercase(),
                                    color      = Color(0xFF444444),
                                    fontSize   = 6.sp,
                                    fontFamily = PixelFontFamily,
                                    textAlign  = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ── Shared small stat box ─────────────────────────────────────────

@Composable
private fun AnalyticStatBox(modifier: Modifier, label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onBackground) {
    Box(
        modifier = modifier
            .border(2.dp, MaterialTheme.colorScheme.primary)
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                value,
                color = valueColor,
                fontSize   = 18.sp,
                fontFamily = PixelFontFamily
            )
            Text(
                label,
                color      = valueColor.copy(alpha = 0.8f),
                fontSize   = 6.sp,
                fontFamily = PixelFontFamily,
                lineHeight = 10.sp
            )
        }
    }
}
