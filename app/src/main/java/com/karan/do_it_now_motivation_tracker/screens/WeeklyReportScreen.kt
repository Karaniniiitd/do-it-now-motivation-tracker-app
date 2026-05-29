package com.karan.do_it_now_motivation_tracker.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun WeeklyReportScreen(viewModel: GoalViewModel) {
    val reports by viewModel.allWeeklyReports.collectAsState()
    val fmt     = SimpleDateFormat("dd MMM", Locale.getDefault())
    val fullFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(44.dp))

        Text(
            "WEEKLY\nREPORT",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize   = 26.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 38.sp,
            letterSpacing = 2.sp
        )

        Text(
            "YOUR PERFORMANCE CARD EACH WEEK",
            color      = Color(0xFF555555),
            fontSize   = 7.sp,
            fontFamily = PixelFontFamily
        )

        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, Color(0xFF333333)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "NO REPORTS YET",
                        color      = Color(0xFF444444),
                        fontSize   = 9.sp,
                        fontFamily = PixelFontFamily
                    )
                    Text(
                        "COMPLETE YOUR FIRST WEEK TO SEE A REPORT",
                        color      = Color(0xFF333333),
                        fontSize   = 7.sp,
                        fontFamily = PixelFontFamily,
                        textAlign  = TextAlign.Center
                    )
                }
            }
        } else {
            reports.forEach { report ->
                val cal = Calendar.getInstance()
                try {
                    cal.time = fullFmt.parse(report.weekStart) ?: cal.time
                } catch (_: Exception) {}
                val weekEndCal = Calendar.getInstance().apply { time = cal.time; add(Calendar.DAY_OF_YEAR, 6) }
                val weekLabel  = "${fmt.format(cal.time)} – ${fmt.format(weekEndCal.time)}"

                val gradeColor = when (report.grade) {
                    "A"  -> Color.White
                    "B"  -> Color(0xFFCCCCCC)
                    "C"  -> Color(0xFF999999)
                    "D"  -> Color(0xFF666666)
                    else -> Color(0xFF444444)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, gradeColor)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Header: week + grade
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    weekLabel.uppercase(),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize   = 9.sp,
                                    fontFamily = PixelFontFamily
                                )
                                Text(
                                    "WEEK REPORT",
                                    color      = Color(0xFF555555),
                                    fontSize   = 7.sp,
                                    fontFamily = PixelFontFamily
                                )
                            }
                            // Giant grade letter
                            Text(
                                report.grade,
                                color      = gradeColor,
                                fontSize   = 48.sp,
                                fontFamily = PixelFontFamily
                            )
                        }

                        // Stats grid
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            WeeklyStatMini(Modifier.weight(1f), "GOALS\nDONE",    "${report.goalsCompleted}")
                            WeeklyStatMini(Modifier.weight(1f), "ACTIVE\nDAYS",   "${report.activeDays}/7")
                            WeeklyStatMini(Modifier.weight(1f), "XP\nEARNED",     "+${report.xpEarned}")
                        }

                        // 7-day bar showing active days
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "DAILY ACTIVITY",
                                color      = Color(0xFF555555),
                                fontSize   = 7.sp,
                                fontFamily = PixelFontFamily
                            )
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                val dayLabels = listOf("M","T","W","T","F","S","S")
                                dayLabels.forEachIndexed { i, label ->
                                    val active = i < report.activeDays
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(24.dp)
                                                .background(if (active) gradeColor else Color(0xFF1A1A1A))
                                        )
                                        Text(label, color = Color(0xFF555555), fontSize = 6.sp, fontFamily = PixelFontFamily)
                                    }
                                }
                            }
                        }

                        // Grade description
                        val desc = when (report.grade) {
                            "A"  -> "OUTSTANDING. PEAK PERFORMANCE."
                            "B"  -> "SOLID WEEK. KEEP PUSHING."
                            "C"  -> "AVERAGE. YOU CAN DO BETTER."
                            "D"  -> "NEEDS IMPROVEMENT. STAY CONSISTENT."
                            else -> "ROUGH WEEK. START FRESH TOMORROW."
                        }
                        Text(
                            desc,
                            color      = Color(0xFF666666),
                            fontSize   = 7.sp,
                            fontFamily = PixelFontFamily
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun WeeklyStatMini(modifier: Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .border(1.dp, Color(0xFF333333))
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontFamily = PixelFontFamily)
            Text(label, color = Color(0xFF555555), fontSize = 6.sp, fontFamily = PixelFontFamily, lineHeight = 10.sp)
        }
    }
}
