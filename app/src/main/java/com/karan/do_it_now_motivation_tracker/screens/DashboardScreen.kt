package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.model.DailyLog
import com.karan.do_it_now_motivation_tracker.model.DailyQuest
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.model.progress
import com.karan.do_it_now_motivation_tracker.model.recurrenceLabel
import com.karan.do_it_now_motivation_tracker.ui.components.Categories
import com.karan.do_it_now_motivation_tracker.ui.components.PixelCategoryIcon
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconCalendar
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconClipboard
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconFlame
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconLineChart
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.util.UserPrefsManager
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val sdf     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
private val dispFmt = SimpleDateFormat("dd MMM",     Locale.getDefault())

private fun weeklyFocus(logs: List<DailyLog>): Int {
    val cutoff    = System.currentTimeMillis() - 7 * 86_400_000L
    val activeDays = logs.count { (sdf.parse(it.date)?.time ?: 0L) >= cutoff && it.goalsCompletedCount > 0 }
    return (activeDays * 100) / 7
}

private fun monthlyDays(logs: List<DailyLog>): Int {
    val cutoff = System.currentTimeMillis() - 30 * 86_400_000L
    return logs.count { (sdf.parse(it.date)?.time ?: 0L) >= cutoff && it.goalsCompletedCount > 0 }
}

@Composable
fun DashboardScreen(navController: NavController, viewModel: GoalViewModel) {
    val context        = LocalContext.current
    val prefs          = remember { UserPrefsManager.getInstance(context) }
    val userName       = remember { prefs.userName }

    val activeGoals    by viewModel.activeGoals.collectAsState()
    val completedCount by viewModel.completedGoalCount.collectAsState()
    val totalGoals     by viewModel.totalGoals.collectAsState()
    val currentStreak  by viewModel.currentStreak.collectAsState()
    val todaysFocus    by viewModel.todaysFocus.collectAsState()
    val yearlyLogs     by viewModel.yearlyLogs.collectAsState()
    val userStats      by viewModel.userStats.collectAsState()

    val goalPercent    = if (totalGoals > 0) (completedCount.toFloat() / totalGoals) else 0f
    val weekly         = remember(yearlyLogs) { weeklyFocus(yearlyLogs) }
    val monthly        = remember(yearlyLogs) { monthlyDays(yearlyLogs) }
    val todaysQuests   by viewModel.todaysQuests.collectAsState()

    // Animate donut ring
    val ringAnim = remember { Animatable(0f) }
    LaunchedEffect(goalPercent) { ringAnim.animateTo(goalPercent, tween(1200)) }

    // Generate today's quests if none exist yet
    LaunchedEffect(Unit) { viewModel.ensureTodaysQuests() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(Modifier.height(44.dp)) }

        // ── HELLO header ──────────────────────────────────────────
        item {
            Text(
                text      = "HELLO,\n$userName",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize  = 36.sp,
                fontFamily = PixelFontFamily,
                lineHeight = 48.sp
            )
        }

        // ── Daily goal ring card ──────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Donut ring with halftone feel (dashed segments)
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(Modifier.fillMaxSize()) {
                            val strokeW = 18.dp.toPx()
                            val r       = (size.minDimension - strokeW) / 2f
                            val tl      = Offset(center.x - r, center.y - r)
                            val sz      = Size(r * 2, r * 2)
                            // Track
                            drawArc(Color(0xFF1A1A1A), -90f, 360f, false, tl, sz,
                                style = Stroke(strokeW, cap = StrokeCap.Square))
                            // Progress — pixel square cap for authenticity
                            if (ringAnim.value > 0.005f) {
                                drawArc(Color.White, -90f, 360f * ringAnim.value, false, tl, sz,
                                    style = Stroke(strokeW, cap = StrokeCap.Square))
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${(goalPercent * 100).toInt()}%",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize   = 28.sp,
                                fontFamily = PixelFontFamily
                            )
                            Text(
                                "DAILY\nGOAL",
                                color      = Color(0xFF888888),
                                fontSize   = 7.sp,
                                fontFamily = PixelFontFamily,
                                lineHeight = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Today's focus
                    val focusTitle = todaysFocus?.title?.uppercase() ?: "ADD A GOAL TO START"
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.width(3.dp).height(18.dp).background(MaterialTheme.colorScheme.primary))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "TODAY'S FOCUS: $focusTitle",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize   = 9.sp,
                            fontFamily = PixelFontFamily,
                            lineHeight = 14.sp,
                            maxLines   = 2,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // ── 2×2 stats grid ────────────────────────────────────────
        item {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    PixelStatBox(Modifier.weight(1f),
                        icon  = { PixelIconFlame(it)    },
                        value = "$currentStreak",
                        unit  = "days",
                        label = "STREAK"
                    )
                    PixelStatBox(Modifier.weight(1f),
                        icon  = { PixelIconClipboard(it) },
                        value = "${activeGoals.size}",
                        unit  = "goals",
                        label = "ACTIVE GOALS"
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    PixelStatBox(Modifier.weight(1f),
                        icon  = { PixelIconLineChart(it) },
                        value = "$weekly%",
                        unit  = "",
                        label = "WEEKLY FOCUS"
                    )
                    PixelStatBox(Modifier.weight(1f),
                        icon  = { PixelIconCalendar(it) },
                        value = "$monthly",
                        unit  = "days",
                        label = "MONTHLY"
                    )
                }
            }
        }

        // ── XP segment bar ────────────────────────────────────────
        item {
            val xp        = userStats.totalXp
            val level     = userStats.level
            val title     = userStats.levelTitle
            val threshold = when { xp < 100 -> 100; xp < 300 -> 300; xp < 600 -> 600; xp < 1000 -> 1000; else -> 2000 }
            val prev      = when { xp < 100 -> 0;   xp < 300 -> 100; xp < 600 -> 300; xp < 1000 -> 600;  else -> 1000 }
            val frac      = ((xp - prev).toFloat() / (threshold - prev)).coerceIn(0f, 1f)
            val segments  = 12
            val filled    = (frac * segments).toInt()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text(
                            "LV $level  ${title.uppercase()}",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize   = 9.sp,
                            fontFamily = PixelFontFamily
                        )
                        Text(
                            "$xp XP",
                            color      = Color(0xFF888888),
                            fontSize   = 9.sp,
                            fontFamily = PixelFontFamily
                        )
                    }
                    // Pixel segment bar
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        repeat(segments) { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .border(1.dp, Color(0xFF444444))
                                    .background(if (i < filled) Color.White else Color.Black)
                            )
                        }
                    }
                }
            }
        }

        // ── Daily Quests widget ───────────────────────────────────
        if (todaysQuests.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "DAILY QUESTS",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize   = 10.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 2.sp
                    )
                    todaysQuests.forEach { quest ->
                        DailyQuestRow(quest)
                    }
                }
            }
        }

        // ── Active goals list preview ─────────────────────────────
        if (activeGoals.isNotEmpty()) {
            item {
                Text(
                    "ACTIVE MISSIONS",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 10.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
            }
            items(activeGoals.take(3)) { goal ->
                GoalRowCard(goal, onClick = { navController.navigate("editGoal/${goal.id}") })
            }
            if (activeGoals.size > 3) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF333333))
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { navController.navigate("goals") }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "VIEW ALL ${activeGoals.size} MISSIONS",
                            color      = Color(0xFF666666),
                            fontSize   = 8.sp,
                            fontFamily = PixelFontFamily,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

// ── Shared: Goal row card (also used by GoalListScreen) ───────────

@Composable
fun GoalRowCard(goal: Goal, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.primary)
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PixelCategoryIcon(
                    category = goal.category,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        goal.title.uppercase(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 9.sp,
                        fontFamily = PixelFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    val recLabel = goal.recurrenceLabel()
                    Text(
                        "Due ${dispFmt.format(Date(goal.endDate))}  ${goal.difficulty.uppercase()}" +
                                if (recLabel.isNotEmpty()) "  ↺$recLabel" else "",
                        color    = Color(0xFF777777),
                        fontSize = 7.sp,
                        fontFamily = PixelFontFamily
                    )
                }
                val diffColor = when (goal.difficulty) {
                    "Boss"   -> Color.White
                    "Hard"   -> Color(0xFFCCCCCC)
                    "Medium" -> Color(0xFF999999)
                    else     -> Color(0xFF666666)
                }
                Box(
                    modifier = Modifier
                        .border(1.dp, diffColor)
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Text(
                        goal.difficulty.uppercase(),
                        color    = diffColor,
                        fontSize = 7.sp,
                        fontFamily = PixelFontFamily
                    )
                }
            }
            // Pixel progress bar (only if partially done)
            if (goal.progressPercent > 0) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val seg    = 10
                    val filled = (goal.progressPercent * seg / 100).coerceIn(0, seg)
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(seg) { i ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .background(if (i < filled) Color.White else Color(0xFF333333))
                            )
                        }
                    }
                    Text(
                        "${goal.progressPercent}%",
                        color    = Color(0xFF888888),
                        fontSize = 6.sp,
                        fontFamily = PixelFontFamily
                    )
                }
            }
        }
    }
}

// ── Pixel stat box ────────────────────────────────────────────────

@Composable
private fun PixelStatBox(
    modifier: Modifier,
    icon: @Composable (Modifier) -> Unit,
    value: String,
    unit: String,
    label: String
) {
    Box(
        modifier = modifier
            .border(2.dp, MaterialTheme.colorScheme.primary)
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp)
    ) {
        Column {
            icon(Modifier.size(26.dp))
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 24.sp,
                    fontFamily = PixelFontFamily
                )
                if (unit.isNotEmpty()) {
                    Spacer(Modifier.width(3.dp))
                    Text(
                        unit,
                        color      = Color(0xFF888888),
                        fontSize   = 7.sp,
                        fontFamily = PixelFontFamily,
                        modifier   = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                label,
                color      = Color(0xFF666666),
                fontSize   = 7.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ── Daily Quest row ───────────────────────────────────────────────

@Composable
private fun DailyQuestRow(quest: DailyQuest) {
    val progress = quest.progress
    val segments = 8
    val filled   = (progress * segments).toInt()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                quest.description,
                color      = if (quest.isCompleted) Color(0xFF555555) else Color.White,
                fontSize   = 8.sp,
                fontFamily = PixelFontFamily,
                modifier   = Modifier.weight(1f)
            )
            Text(
                if (quest.isCompleted) "DONE" else "+${quest.xpReward}XP",
                color      = if (quest.isCompleted) Color(0xFF555555) else Color(0xFF888888),
                fontSize   = 7.sp,
                fontFamily = PixelFontFamily
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            repeat(segments) { i ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .background(
                            when {
                                quest.isCompleted -> Color(0xFF444444)
                                i < filled        -> Color.White
                                else              -> MaterialTheme.colorScheme.surface
                            }
                        )
                )
            }
        }
        Text(
            "${quest.currentValue}/${quest.targetValue} COMPLETED",
            color      = Color(0xFF555555),
            fontSize   = 6.sp,
            fontFamily = PixelFontFamily
        )
    }
}