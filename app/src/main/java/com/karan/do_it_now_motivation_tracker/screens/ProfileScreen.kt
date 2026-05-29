package com.karan.do_it_now_motivation_tracker.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.ui.components.ActivityCalendar
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconCrown
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconFlame
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconLightning
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconShield
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconStopwatch
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.util.FirebaseManager
import com.karan.do_it_now_motivation_tracker.util.ShareManager
import com.karan.do_it_now_motivation_tracker.util.UserPrefsManager
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ProfileScreen(navController: NavController, viewModel: GoalViewModel) {
    val context        = LocalContext.current
    val prefs          = remember { UserPrefsManager.getInstance(context) }

    val completedGoals by viewModel.completedGoals.collectAsState()
    val currentStreak  by viewModel.currentStreak.collectAsState()
    val longestStreak  by viewModel.longestStreak.collectAsState()
    val yearlyLogs     by viewModel.yearlyLogs.collectAsState()
    val userStats      by viewModel.userStats.collectAsState()

    val completedCount = completedGoals.size
    val totalFocusHrs  = remember(yearlyLogs) {
        yearlyLogs.sumOf { it.goalsCompletedCount } // approximation
    }

    // Weekly bar data (Mon-Sun)
    val weeklyBars = remember(yearlyLogs) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        (0..6).map { daysBack ->
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -(6 - daysBack))
            val dateStr = sdf.format(cal.time)
            val log = yearlyLogs.find { it.date == dateStr }
            (log?.goalsCompletedCount ?: 0).coerceAtMost(10)
        }
    }
    val maxBar = weeklyBars.maxOrNull()?.coerceAtLeast(1) ?: 1

    // Discipline score (0-100) based on streak + completions
    val disciplineScore = ((currentStreak * 3 + completedCount * 2).coerceAtMost(100))

    // Badges — earned / locked
    data class Badge(
        val label: String,
        val earned: Boolean,
        val icon: @Composable (Modifier, Color) -> Unit
    )
    val badges = listOf(
        Badge("DISCIPLINED", completedCount >= 5,  { m, c -> PixelIconShield(m, c) }),
        Badge("CONSISTENT",  currentStreak >= 7,   { m, c -> PixelIconStopwatch(m, c) }),
        Badge("ACHIEVER",    completedCount >= 10,  { m, c -> PixelIconLightning(m, c) }),
        Badge("ON FIRE",     currentStreak >= 14,  { m, c -> PixelIconFlame(m, c) }),
        Badge("LEGEND",      userStats.level >= 5, { m, c -> PixelIconCrown(m, c) })
    )

    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor   = Color.Black,
            title = {
                Text(
                    "CLEAR ALL DATA?",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize   = 13.sp
                )
            },
            text = {
                Text(
                    "This will delete all goals and stats.",
                    color      = Color(0xFF888888),
                    fontFamily = PixelFontFamily,
                    fontSize   = 9.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                    showClearDialog = false
                }) {
                    Text("CONFIRM", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("CANCEL", color = Color(0xFF888888), fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            }
        )
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

        // ── HELLO header ──────────────────────────────────────────
        Text(
            "HELLO,\n${prefs.userName}",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize   = 36.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 48.sp
        )

        // ── Discipline score halftone circle ──────────────────────
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val bubbleSize = 220.dp
            Box(Modifier.size(bubbleSize), contentAlignment = Alignment.Center) {
                // Halftone dot background
                Canvas(Modifier.fillMaxSize()) {
                    val cx = size.width / 2; val cy = size.height / 2
                    val r  = size.minDimension / 2
                    val step = 12f
                    var row = 0
                    var y = cy - r
                    while (y <= cy + r) {
                        var x = cx - r
                        while (x <= cx + r) {
                            val dist = kotlin.math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy))
                            if (dist < r) {
                                val alpha = 1f - (dist / r)
                                val dotR  = (alpha * 4f).coerceAtLeast(0.8f)
                                drawCircle(Color(0xFF333333), dotR, Offset(x, y))
                            }
                            x += step
                        }
                        y += step; row++
                    }
                    // Progress arc
                    val sw    = 16.dp.toPx()
                    val arcR  = r - sw - 6.dp.toPx()
                    val arcTL = Offset(cx - arcR, cy - arcR)
                    val arcSz = Size(arcR * 2, arcR * 2)
                    drawArc(MaterialTheme.colorScheme.surface, -90f, 360f, false, arcTL, arcSz,
                        style = Stroke(sw, cap = StrokeCap.Square))
                    val sweep = 360f * (disciplineScore / 100f)
                    if (sweep > 0f) {
                        drawArc(Color.White, -90f, sweep, false, arcTL, arcSz,
                            style = Stroke(sw, cap = StrokeCap.Square))
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "DISCIPLINE\nSCORE:",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize   = 9.sp,
                        fontFamily = PixelFontFamily,
                        textAlign  = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "$disciplineScore",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize   = 48.sp,
                        fontFamily = PixelFontFamily
                    )
                }
            }
        }

        // ── BADGES ────────────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "BADGES",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize   = 12.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 1.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                badges.take(3).forEach { badge ->
                    BadgeTile(
                        label   = badge.label,
                        earned  = badge.earned,
                        icon    = badge.icon,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                badges.drop(3).forEach { badge ->
                    BadgeTile(
                        label   = badge.label,
                        earned  = badge.earned,
                        icon    = badge.icon,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Empty spacer to keep alignment if < 3 in last row
                if (badges.size % 3 == 1) {
                    Spacer(Modifier.weight(2f))
                } else if (badges.size % 3 == 2) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        // ── RECENT ACTIVITY chart ─────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "RECENT ACTIVITY",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize   = 12.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 1.sp
            )
            val dayLabels = listOf("MON","TUE","WED","THU","FRI","SAT","SUN")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column(Modifier.fillMaxSize()) {
                    // Bar chart
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        Canvas(Modifier.fillMaxSize()) {
                            val barCount = weeklyBars.size
                            val totalW   = size.width
                            val barW     = (totalW / barCount) * 0.7f
                            val gap      = (totalW / barCount) * 0.3f
                            weeklyBars.forEachIndexed { i, v ->
                                val barH = if (maxBar > 0) (v.toFloat() / maxBar) * size.height else 0f
                                val x    = i * (barW + gap) + gap / 2
                                val y    = size.height - barH
                                drawRect(Color.White, Offset(x, y), Size(barW, barH))
                            }
                        }
                    }
                    // Day labels
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        dayLabels.forEach { d ->
                            Text(
                                d,
                                color      = Color(0xFF888888),
                                fontSize   = 6.sp,
                                fontFamily = PixelFontFamily
                            )
                        }
                    }
                }
            }
        }

        // ── DISCIPLINE HEATMAP ────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "DISCIPLINE HEATMAP",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize   = 12.sp,
                fontFamily = PixelFontFamily,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth(),
                letterSpacing = 1.sp
            )
            ActivityCalendar(logs = yearlyLogs)
        }

        // ── Streak + Focus stats ──────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Longest streak
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp)
            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                    // Mini pixel bar graph
                    Canvas(
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        val bars   = 12
                        val bw     = size.width / bars
                        val maxH   = size.height
                        repeat(bars) { i ->
                            val h = (maxH * ((i + 1f) / bars)).coerceAtLeast(2f)
                            drawRect(
                                Color(0xFF333333),
                                Offset(i * bw + 1f, maxH - h),
                                Size(bw - 2f, h)
                            )
                        }
                        // highlight last few bars
                        val filled = ((longestStreak.toFloat() / 56f) * bars).toInt().coerceIn(0, bars)
                        repeat(filled) { i ->
                            val h = (maxH * ((i + 1f) / bars)).coerceAtLeast(2f)
                            drawRect(
                                Color.White,
                                Offset(i * bw + 1f, maxH - h),
                                Size(bw - 2f, h)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "$longestStreak",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize   = 28.sp,
                            fontFamily = PixelFontFamily
                        )
                        Text(
                            "DAYS",
                            color      = Color(0xFF888888),
                            fontSize   = 7.sp,
                            fontFamily = PixelFontFamily,
                            modifier   = Modifier.padding(start = 4.dp, bottom = 5.dp)
                        )
                    }
                    Text(
                        "LONGEST STREAK",
                        color      = Color(0xFF666666),
                        fontSize   = 6.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Total focus
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp)
            ) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                    // Mini area chart
                    Canvas(
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        val pts = weeklyBars.toList()
                        if (pts.size > 1 && pts.maxOrNull()!! > 0) {
                            val maxV = pts.max().toFloat()
                            val stepX = size.width / (pts.size - 1)
                            val path = androidx.compose.ui.graphics.Path().apply {
                                pts.forEachIndexed { i, v ->
                                    val x = i * stepX
                                    val y = size.height - (v / maxV) * size.height
                                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                                }
                            }
                            drawPath(path, Color(0xFF555555),
                                style = Stroke(2.dp.toPx(), cap = StrokeCap.Square))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "$totalFocusHrs",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize   = 28.sp,
                            fontFamily = PixelFontFamily
                        )
                        Text(
                            "HRS",
                            color      = Color(0xFF888888),
                            fontSize   = 7.sp,
                            fontFamily = PixelFontFamily,
                            modifier   = Modifier.padding(start = 4.dp, bottom = 5.dp)
                        )
                    }
                    Text(
                        "TOTAL FOCUS HOURS",
                        color      = Color(0xFF666666),
                        fontSize   = 6.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // ── Action buttons row ───────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Share stats
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        ShareManager.shareStats(
                            context        = context,
                            userName       = prefs.userName,
                            level          = userStats.level,
                            levelTitle     = userStats.levelTitle,
                            totalXp        = userStats.totalXp,
                            currentStreak  = currentStreak,
                            completedGoals = completedCount,
                            totalGoals     = completedCount // completed is total shown
                        )
                    }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("SHARE STATS", color = MaterialTheme.colorScheme.background, fontSize = 8.sp, fontFamily = PixelFontFamily, letterSpacing = 1.sp)
            }

            // Character
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable { navController.navigate("character") }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("CHARACTER", color = MaterialTheme.colorScheme.onBackground, fontSize = 8.sp, fontFamily = PixelFontFamily, letterSpacing = 1.sp)
            }
        }

        // Weekly Report
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .background(MaterialTheme.colorScheme.background)
                .clickable { navController.navigate("weeklyReport") }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("WEEKLY REPORT", color = MaterialTheme.colorScheme.onBackground, fontSize = 9.sp, fontFamily = PixelFontFamily, letterSpacing = 2.sp)
        }

        // ── Theme toggle ──────────────────────────────────────────────
        val currentTheme = prefs.themeMode
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .background(MaterialTheme.colorScheme.background)
                .clickable {
                    val next = when (currentTheme) {
                        UserPrefsManager.ThemeMode.SYSTEM -> UserPrefsManager.ThemeMode.LIGHT
                        UserPrefsManager.ThemeMode.LIGHT -> UserPrefsManager.ThemeMode.DARK
                        UserPrefsManager.ThemeMode.DARK -> UserPrefsManager.ThemeMode.SYSTEM
                    }
                    prefs.themeMode = next
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("THEME: ${currentTheme.name}", color = MaterialTheme.colorScheme.onBackground, fontSize = 9.sp, fontFamily = PixelFontFamily, letterSpacing = 2.sp)
        }

        // ── Firebase: Login status + Sign In/Out button ────────────────
        val isLoggedIn = FirebaseManager.isLoggedIn
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (isLoggedIn) Color(0xFF333333) else Color.White)
                .background(if (isLoggedIn) Color.Black else Color.White)
                .clickable {
                    if (isLoggedIn) {
                        FirebaseManager.signOut(context)
                    } else {
                        navController.navigate("login")
                    }
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (isLoggedIn) "SIGNED IN  ●  SIGN OUT" else "SIGN IN WITH GOOGLE",
                    color      = if (isLoggedIn) Color(0xFF555555) else Color.Black,
                    fontSize   = 9.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
                if (isLoggedIn) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        FirebaseManager.email.uppercase().take(28),
                        color = Color(0xFF333333), fontSize = 6.sp, fontFamily = PixelFontFamily
                    )
                }
            }
        }

        // ── Leaderboard button ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF333333))
                .background(MaterialTheme.colorScheme.background)
                .clickable { navController.navigate("leaderboard") }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "GLOBAL LEADERBOARD",
                color      = Color(0xFF666666),
                fontSize   = 9.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 2.sp
            )
        }

        // ── Clear data button ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF333333))
                .background(MaterialTheme.colorScheme.background)
                .clickable { showClearDialog = true }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "CLEAR ALL DATA",
                color      = Color(0xFF444444),
                fontSize   = 9.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 2.sp
            )
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ── Badge tile composable ─────────────────────────────────────────

@Composable
private fun BadgeTile(
    label: String,
    earned: Boolean,
    icon: @Composable (Modifier, Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (earned) Color.White else Color(0xFF333333)
    val iconColor   = if (earned) Color.White else Color(0xFF333333)
    val textColor   = if (earned) Color.White else Color(0xFF444444)

    Box(
        modifier = modifier
            .border(2.dp, borderColor)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            icon(Modifier.size(36.dp), iconColor)
            Text(
                label,
                color      = textColor,
                fontSize   = 6.sp,
                fontFamily = PixelFontFamily,
                textAlign  = TextAlign.Center,
                letterSpacing = 0.5.sp
            )
        }
    }
}