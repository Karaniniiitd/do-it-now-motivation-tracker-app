package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.model.UserStats
import com.karan.do_it_now_motivation_tracker.model.xpToLevelInfo
import com.karan.do_it_now_motivation_tracker.ui.components.ActivityCalendar
import com.karan.do_it_now_motivation_tracker.ui.components.Categories
import com.karan.do_it_now_motivation_tracker.ui.theme.AtmosphericBlack
import com.karan.do_it_now_motivation_tracker.ui.theme.CardBorder
import com.karan.do_it_now_motivation_tracker.ui.theme.CardBorderBright
import com.karan.do_it_now_motivation_tracker.ui.theme.CyanGlow
import com.karan.do_it_now_motivation_tracker.ui.theme.DimText
import com.karan.do_it_now_motivation_tracker.ui.theme.ElectricBlue
import com.karan.do_it_now_motivation_tracker.ui.theme.GlowOrange
import com.karan.do_it_now_motivation_tracker.ui.theme.MidnightCard
import com.karan.do_it_now_motivation_tracker.ui.theme.MutedPurple
import com.karan.do_it_now_motivation_tracker.ui.theme.MutedText
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftGreen
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftIndigo
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftRed
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftWhite
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftYellow
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val quotes = listOf(
    "The secret of getting ahead is getting started.",
    "It always seems impossible until it's done.",
    "Small daily improvements lead to stunning results.",
    "Discipline is choosing what you want most.",
    "Push yourself, because no one else will.",
    "You are never too old to set another goal.",
    "A year from now you'll wish you started today.",
    "Success is built on consistency.",
    "Every day is a chance to get better.",
    "The pain of discipline weighs ounces."
)

private fun greeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11  -> "Good morning"
        in 12..16 -> "Good afternoon"
        else      -> "Good evening"
    }
}

fun calculateProgress(start: Long, end: Long): Float {
    val now = System.currentTimeMillis()
    if (end <= start) return 0f
    return ((now - start).toFloat() / (end - start).toFloat()).coerceIn(0f, 1f)
}

@Composable
fun DashboardScreen(navController: NavController, viewModel: GoalViewModel) {
    val activeGoals    by viewModel.activeGoals.collectAsState()
    val completedGoals by viewModel.completedGoals.collectAsState()
    val currentStreak  by viewModel.currentStreak.collectAsState()
    val totalGoals     by viewModel.totalGoals.collectAsState()
    val completedCount by viewModel.completedGoalCount.collectAsState()
    val todaysFocus    by viewModel.todaysFocus.collectAsState()
    val yearlyLogs     by viewModel.yearlyLogs.collectAsState()
    val userStats      by viewModel.userStats.collectAsState()

    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val quoteIdx = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % quotes.size }
    var tab by remember { mutableIntStateOf(0) }

    val inf = rememberInfiniteTransition(label = "dash")
    val glowShift by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse), "glow"
    )

    Box(Modifier.fillMaxSize().background(AtmosphericBlack)) {
        // Ambient background orbs
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                Brush.radialGradient(
                    listOf(SoftIndigo.copy(alpha = 0.06f), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * (0.1f + glowShift * 0.08f)),
                    radius = size.width * 0.55f
                ),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.85f, size.height * (0.1f + glowShift * 0.08f))
            )
            drawCircle(
                Brush.radialGradient(
                    listOf(CyanGlow.copy(alpha = 0.04f), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * (0.65f - glowShift * 0.08f)),
                    radius = size.width * 0.45f
                ),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.15f, size.height * (0.65f - glowShift * 0.08f))
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Greeting ──────────────────────────────────────
            item {
                Spacer(Modifier.height(48.dp))
                Text(
                    greeting(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = SoftWhite
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    quotes[quoteIdx],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    lineHeight = 20.sp
                )
            }

            // ── XP / Level bar ────────────────────────────────
            item { XpLevelBar(userStats) }

            // ── Today's Focus ─────────────────────────────────
            todaysFocus?.let { focus ->
                item { TodayFocusCard(focus, fmt, onComplete = { viewModel.completeGoal(focus) }) }
            }

            // ── Stats row ─────────────────────────────────────
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlowMiniCard(Modifier.weight(1f), "🔥", "Streak", "$currentStreak days", GlowOrange)
                    GlowMiniCard(Modifier.weight(1f), "✅", "Done", "$completedCount / $totalGoals", SoftGreen)
                    GlowMiniCard(Modifier.weight(1f), "📋", "Active", "${activeGoals.size}", CyanGlow)
                }
            }

            // ── Activity calendar ─────────────────────────────
            item { ActivityCalendar(yearlyLogs) }

            // ── Tabs ──────────────────────────────────────────
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AtmosphericTab("Active · ${activeGoals.size}", tab == 0) { tab = 0 }
                    AtmosphericTab("Done · ${completedGoals.size}", tab == 1) { tab = 1 }
                }
            }

            // ── Goal list ─────────────────────────────────────
            val goals = if (tab == 0) activeGoals else completedGoals
            if (goals.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().height(80.dp), Alignment.Center) {
                        Text(
                            if (tab == 0) "No active goals — tap + to begin" else "Nothing completed yet",
                            style = MaterialTheme.typography.bodyMedium, color = DimText
                        )
                    }
                }
            }
            items(goals, key = { it.id }) { goal ->
                GoalCard(
                    goal = goal, formatter = fmt,
                    onComplete   = { viewModel.completeGoal(goal) },
                    onUncomplete = { viewModel.uncompleteGoal(goal) },
                    onEdit       = { navController.navigate("editGoal/${goal.id}") },
                    onDelete     = { viewModel.deleteGoal(goal) }
                )
            }

            item { Spacer(Modifier.height(90.dp)) }
        }
    }
}

// ── XP Level Bar ──────────────────────────────────────────────
@Composable
private fun XpLevelBar(stats: UserStats) {
    val (level, title, progress) = xpToLevelInfo(stats.totalXp)
    val (currentProg, maxProg) = progress
    val fraction = if (maxProg > 0) currentProg.toFloat() / maxProg else 1f

    val levelColor = when (level) {
        1 -> MutedText; 2 -> SoftGreen; 3 -> CyanGlow; 4 -> ElectricBlue; 5 -> MutedPurple; else -> GlowOrange
    }

    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MidnightCard.copy(alpha = 0.5f))
            .border(1.dp, levelColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(levelColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Lv $level", style = MaterialTheme.typography.labelLarge, color = levelColor, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium, color = SoftWhite)
                }
                Text(
                    "${stats.totalXp} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { fraction.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                color = levelColor,
                trackColor = CardBorder.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "$currentProg / $maxProg XP to next level",
                style = MaterialTheme.typography.bodySmall,
                color = DimText
            )
        }
    }
}

// ── Today's Focus Card ────────────────────────────────────────
@Composable
private fun TodayFocusCard(goal: Goal, formatter: SimpleDateFormat, onComplete: () -> Unit) {
    val catColor = Categories.color(goal.category)
    val daysLeft = ((goal.endDate - System.currentTimeMillis()) / 86400000L).coerceAtLeast(0)

    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(catColor.copy(alpha = 0.12f), MidnightCard.copy(alpha = 0.5f))
                )
            )
            .border(1.dp, catColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    "📌  Today's Focus",
                    style = MaterialTheme.typography.labelSmall,
                    color = catColor,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(catColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        "${Categories.emoji(goal.category)} ${goal.category}",
                        style = MaterialTheme.typography.labelSmall,
                        color = catColor
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(goal.title, style = MaterialTheme.typography.titleLarge, color = SoftWhite)
            Spacer(Modifier.height(6.dp))
            Text(
                "$daysLeft days remaining · due ${formatter.format(Date(goal.endDate))}",
                style = MaterialTheme.typography.bodySmall, color = MutedText
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftGreen.copy(alpha = 0.12f))
                    .border(1.dp, SoftGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onComplete)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = SoftGreen, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Mark Complete", style = MaterialTheme.typography.labelLarge, color = SoftGreen)
            }
        }
    }
}

// ── Mini stat card ────────────────────────────────────────────
@Composable
private fun GlowMiniCard(modifier: Modifier, emoji: String, label: String, value: String, glowColor: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MidnightCard.copy(alpha = 0.45f))
            .border(1.dp, glowColor.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = glowColor)
            Text(label, style = MaterialTheme.typography.labelSmall, color = DimText, letterSpacing = 0.5.sp)
        }
    }
}

// ── Tab button ────────────────────────────────────────────────
@Composable
private fun AtmosphericTab(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) CyanGlow.copy(alpha = 0.1f) else Color.Transparent)
            .border(1.dp, if (selected) CyanGlow.copy(alpha = 0.3f) else CardBorder.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = if (selected) CyanGlow else DimText)
    }
}

// ── Goal Card ─────────────────────────────────────────────────
@Composable
private fun GoalCard(
    goal: Goal, formatter: SimpleDateFormat,
    onComplete: () -> Unit, onUncomplete: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit
) {
    val progress   = calculateProgress(goal.startDate, goal.endDate)
    val catColor   = Categories.color(goal.category)
    val diffColor  = when (goal.difficulty) { "Easy" -> SoftGreen; "Medium" -> SoftYellow; "Hard" -> SoftRed; else -> MutedText }
    val borderGlow = if (goal.isCompleted) SoftGreen.copy(alpha = 0.12f) else catColor.copy(alpha = 0.1f)

    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MidnightCard.copy(alpha = 0.45f))
            .border(1.dp, borderGlow, RoundedCornerShape(18.dp))
            .animateContentSize(tween(300))
    ) {
        // Left accent stripe
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                .background(Brush.verticalGradient(listOf(catColor, catColor.copy(alpha = 0.1f))))
                .align(Alignment.CenterStart)
        )

        Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${Categories.emoji(goal.category)} ",
                            fontSize = 14.sp
                        )
                        Text(
                            goal.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (goal.isCompleted) DimText else SoftWhite,
                            textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else null,
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${formatter.format(Date(goal.startDate))} → ${formatter.format(Date(goal.endDate))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DimText
                    )
                }

                Row {
                    if (!goal.isCompleted) {
                        IconButton(onClick = onComplete, Modifier.size(32.dp)) {
                            Icon(Icons.Default.CheckCircle, null, tint = SoftGreen.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onEdit, Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, null, tint = MutedText.copy(alpha = 0.5f), modifier = Modifier.size(15.dp))
                        }
                    } else {
                        IconButton(onClick = onUncomplete, Modifier.size(32.dp)) {
                            Icon(Icons.Default.CheckCircle, null, tint = CyanGlow.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(onClick = onDelete, Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, null, tint = SoftRed.copy(alpha = 0.35f), modifier = Modifier.size(15.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { if (goal.isCompleted) 1f else progress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = if (goal.isCompleted) SoftGreen.copy(alpha = 0.6f) else catColor.copy(alpha = 0.7f),
                trackColor = CardBorder.copy(alpha = 0.25f),
                strokeCap = StrokeCap.Round
            )

            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(
                    if (goal.isCompleted) "Completed ✓" else "${(progress * 100).toInt()}% elapsed",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (goal.isCompleted) SoftGreen.copy(alpha = 0.7f) else MutedText
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagChip(goal.difficulty, diffColor)
                }
            }
        }
    }
}

@Composable
private fun TagChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.9f))
    }
}