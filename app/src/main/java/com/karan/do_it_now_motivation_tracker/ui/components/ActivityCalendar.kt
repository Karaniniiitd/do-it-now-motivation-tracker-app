package com.karan.do_it_now_motivation_tracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.model.DailyLog
import com.karan.do_it_now_motivation_tracker.ui.theme.CardBorder
import com.karan.do_it_now_motivation_tracker.ui.theme.CyanGlow
import com.karan.do_it_now_motivation_tracker.ui.theme.DimText
import com.karan.do_it_now_motivation_tracker.ui.theme.HeatLevel0
import com.karan.do_it_now_motivation_tracker.ui.theme.HeatLevel1
import com.karan.do_it_now_motivation_tracker.ui.theme.HeatLevel2
import com.karan.do_it_now_motivation_tracker.ui.theme.HeatLevel3
import com.karan.do_it_now_motivation_tracker.ui.theme.HeatLevel4
import com.karan.do_it_now_motivation_tracker.ui.theme.MidnightCard
import com.karan.do_it_now_motivation_tracker.ui.theme.MutedText
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private fun heatColor(count: Int): Color = when {
    count <= 0 -> HeatLevel0
    count == 1 -> HeatLevel1
    count == 2 -> HeatLevel2
    count == 3 -> HeatLevel3
    else       -> HeatLevel4
}

private val dayLabels = listOf("", "Mon", "", "Wed", "", "Fri", "")
private val monthFmt  = SimpleDateFormat("MMM", Locale.getDefault())
private val dateFmt   = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
private val displayFmt = SimpleDateFormat("MMM d", Locale.getDefault())

@Composable
fun ActivityCalendar(logs: List<DailyLog>) {
    val logMap = remember(logs) { logs.associateBy { it.date } }
    var tooltip by remember { mutableStateOf<String?>(null) }

    // Build 52-week grid starting from 52 weeks ago aligned to Sunday
    val today = Calendar.getInstance()
    val startCal = Calendar.getInstance().apply {
        add(Calendar.WEEK_OF_YEAR, -51)
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    }

    // Collect weeks: each week = list of 7 dates (Sun–Sat)
    val weeks = mutableListOf<List<String>>()
    val monthLabels = mutableListOf<Pair<Int, String>>() // (weekIdx, monthLabel)
    val tempCal = startCal.clone() as Calendar

    var lastMonth = -1
    repeat(52) { weekIdx ->
        val week = mutableListOf<String>()
        val weekStartMonth = tempCal.get(Calendar.MONTH)
        if (weekStartMonth != lastMonth) {
            monthLabels.add(weekIdx to monthFmt.format(tempCal.time))
            lastMonth = weekStartMonth
        }
        repeat(7) {
            week.add(dateFmt.format(tempCal.time))
            tempCal.add(Calendar.DAY_OF_YEAR, 1)
        }
        weeks.add(week)
    }

    val cellSize = 10.dp
    val cellGap  = 2.dp
    val cellStep = cellSize + cellGap
    val leftPad  = 28.dp // room for day labels

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Activity",
            style = MaterialTheme.typography.titleLarge,
            color = SoftWhite
        )
        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MidnightCard.copy(alpha = 0.4f))
                .border(1.dp, CardBorder.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                // Scrollable heatmap
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    // Day-of-week labels column
                    Column(modifier = Modifier.padding(top = 14.dp)) {
                        dayLabels.forEach { label ->
                            Box(
                                modifier = Modifier.size(cellSize, cellSize).padding(bottom = cellGap),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (label.isNotEmpty()) {
                                    Text(
                                        label,
                                        fontSize = 8.sp,
                                        color = DimText,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                }
                            }
                            if (label.isNotEmpty()) Spacer(Modifier.height(cellGap))
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    // Grid columns
                    Column {
                        // Month labels row
                        Row {
                            weeks.forEachIndexed { weekIdx, _ ->
                                val label = monthLabels.find { it.first == weekIdx }?.second ?: ""
                                Box(modifier = Modifier.width(cellStep)) {
                                    if (label.isNotEmpty()) {
                                        Text(label, fontSize = 8.sp, color = MutedText)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(2.dp))

                        // Cells grid
                        Row(horizontalArrangement = Arrangement.spacedBy(cellGap)) {
                            weeks.forEach { week ->
                                Column(verticalArrangement = Arrangement.spacedBy(cellGap)) {
                                    week.forEach { dateStr ->
                                        val count = logMap[dateStr]?.goalsCompletedCount ?: 0
                                        val isToday = dateStr == dateFmt.format(Date())
                                        Box(
                                            modifier = Modifier
                                                .size(cellSize)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(heatColor(count))
                                                .then(
                                                    if (isToday) Modifier.border(
                                                        1.dp,
                                                        CyanGlow.copy(alpha = 0.7f),
                                                        RoundedCornerShape(2.dp)
                                                    ) else Modifier
                                                )
                                                .pointerInput(dateStr) {
                                                    detectTapGestures {
                                                        val parsedDate = runCatching {
                                                            dateFmt.parse(dateStr)?.let { displayFmt.format(it) }
                                                        }.getOrNull() ?: dateStr
                                                        tooltip = if (count > 0) "$count goal${if (count > 1) "s" else ""} on $parsedDate"
                                                        else "No goals on $parsedDate"
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Tooltip
                tooltip?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanGlow.copy(alpha = 0.8f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Less", style = MaterialTheme.typography.labelSmall, color = DimText)
                    listOf(HeatLevel0, HeatLevel1, HeatLevel2, HeatLevel3, HeatLevel4).forEach { clr ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(clr)
                        )
                    }
                    Text("More", style = MaterialTheme.typography.labelSmall, color = DimText)
                }
            }
        }
    }
}
