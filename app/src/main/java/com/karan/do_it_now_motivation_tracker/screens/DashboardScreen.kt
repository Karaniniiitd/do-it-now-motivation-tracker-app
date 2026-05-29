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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.karan.do_it_now_motivation_tracker.model.xpReward
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
    val recentLogs = logs.filter { (sdf.parse(it.date)?.time ?: 0L) >= cutoff }
    val activeDays = recentLogs.count { it.goalsCompletedCount > 0 }
    val failedCount = recentLogs.sumOf { it.goalsFailedCount }
    val baseFocus = (activeDays * 100) / 7
    return (baseFocus - (failedCount * 5)).coerceAtLeast(0)
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
    val failedCount    by viewModel.failedGoalCount.collectAsState()
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

    var actionDialogGoal by remember { androidx.compose.runtime.mutableStateOf<Goal?>(null) }

    if (actionDialogGoal != null) {
        val goal = actionDialogGoal!!
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { actionDialogGoal = null },
            containerColor   = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "MISSION ACTION",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize = 13.sp
                )
            },
            text = {
                Text(
                    "WHAT WOULD YOU LIKE TO DO WITH \"${goal.title.uppercase()}\"?",
                    color = Color(0xFF888888),
                    fontFamily = PixelFontFamily,
                    fontSize = 9.sp,
                    lineHeight = 16.sp
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    viewModel.completeGoal(goal)
                    val xp = goal.xpReward()
                    val msg = "MISSION COMPLETED! GAINED $xp XP. OUTSTANDING WORK!"
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                    actionDialogGoal = null
                }) {
                    Text("COMPLETE", color = MaterialTheme.colorScheme.primary, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            },
            dismissButton = {
                Row {
                    androidx.compose.material3.TextButton(onClick = {
                        val msg = viewModel.failGoal(goal)
                        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                        actionDialogGoal = null
                    }) {
                        Text("FAIL", color = Color(0xFFFF4444), fontFamily = PixelFontFamily, fontSize = 9.sp)
                    }
                    androidx.compose.material3.TextButton(onClick = {
                        actionDialogGoal = null
                        navController.navigate("editGoal/${goal.id}")
                    }) {
                        Text("EDIT", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 9.sp)
                    }
                }
            }
        )
    }

    val focusGoalId by viewModel.focusGoalId.collectAsState()
    LaunchedEffect(focusGoalId, activeGoals) {
        if (focusGoalId != null && activeGoals.isNotEmpty()) {
            val focusGoal = activeGoals.find { it.id == focusGoalId }
            if (focusGoal != null) {
                actionDialogGoal = focusGoal
            }
            viewModel.setFocusGoalId(null)
        }
    }

    // Generate today's quests if none exist yet
    LaunchedEffect(Unit) { viewModel.ensureTodaysQuests() }

    Box(modifier = Modifier.fillMaxSize()) {
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    PixelStatBox(Modifier.weight(1f),
                        icon  = { PixelIconFlame(it) },
                        value = "$failedCount",
                        unit  = "missions",
                        label = "FAILED",
                        valueColor = Color(0xFFFF4444),
                        borderColor = Color(0xFFFF4444)
                    )
                    PixelStatBox(Modifier.weight(1f),
                        icon  = { PixelIconClipboard(it) },
                        value = "${userStats.freezeTokens}",
                        unit  = "tokens",
                        label = "FREEZES"
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

        // ── Active Timers widget ──────────────────────────────────
        val now = System.currentTimeMillis()
        val runningTimers = activeGoals.filter {
            it.hasTime && now >= it.startDate && now <= it.endDate
        }
        if (runningTimers.isNotEmpty()) {
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
                        "ACTIVE TIMERS",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize   = 10.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 2.sp
                    )
                    runningTimers.forEach { timerGoal ->
                        ActiveTimerRow(timerGoal, onClick = { actionDialogGoal = timerGoal })
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
                GoalRowCard(goal, onClick = { actionDialogGoal = goal })
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

        // ── FLOATING BUTTONS (Top Right) ──────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 44.dp, end = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Settings FAB
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .clickable { navController.navigate("settings") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }

            // AI FAB
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, MaterialTheme.colorScheme.background)
                    .clickable { navController.navigate("ai_chat") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Companion",
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
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
    label: String,
    valueColor: Color = MaterialTheme.colorScheme.onBackground,
    borderColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .border(2.dp, borderColor)
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp)
    ) {
        Column {
            icon(Modifier.size(26.dp))
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    color = valueColor,
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
                color      = valueColor,
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

// ── Active Timer row ─────────────────────────────────────────────

@Composable
private fun ActiveTimerRow(goal: Goal, onClick: () -> Unit) {
    var now by remember { androidx.compose.runtime.mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            now = System.currentTimeMillis()
        }
    }

    val totalDuration = goal.endDate - goal.startDate
    val elapsed = now - goal.startDate
    val remaining = goal.endDate - now
    
    val progress = if (totalDuration > 0) (elapsed.toFloat() / totalDuration).coerceIn(0f, 1f) else 1f

    val hours = (remaining / 3_600_000).toInt().coerceAtLeast(0)
    val minutes = ((remaining % 3_600_000) / 60_000).toInt().coerceAtLeast(0)
    val seconds = ((remaining % 60_000) / 1000).toInt().coerceAtLeast(0)

    val timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, Color(0xFF444444))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                goal.title.uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 11.sp,
                fontFamily = PixelFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(6.dp)
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF444444))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
        
        Text(
            timeString,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontFamily = PixelFontFamily
        )
    }
}