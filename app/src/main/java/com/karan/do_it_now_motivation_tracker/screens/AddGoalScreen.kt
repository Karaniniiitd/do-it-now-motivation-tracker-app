package com.karan.do_it_now_motivation_tracker.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.ui.components.Categories
import com.karan.do_it_now_motivation_tracker.ui.components.PixelCategoryIcon
import com.karan.do_it_now_motivation_tracker.ui.components.PixelDifficultyIcon
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

private data class DiffChip(val label: String, val key: String)

private val addDifficulties = listOf(
    DiffChip("EASY",   "Easy"),
    DiffChip("MEDIUM", "Medium"),
    DiffChip("HARD",   "Hard"),
    DiffChip("BOSS",   "Boss")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(navController: NavController, viewModel: GoalViewModel) {
    val context = LocalContext.current
    val fmt     = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var title           by remember { mutableStateOf("") }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis   by remember { mutableStateOf<Long?>(null) }
    var startDateText   by remember { mutableStateOf("") }
    var endDateText     by remember { mutableStateOf("") }
    var difficulty      by remember { mutableStateOf("Easy") }
    var category        by remember { mutableStateOf(Categories.all.first()) }
    var showStartPicker  by remember { mutableStateOf(false) }
    var showEndPicker    by remember { mutableStateOf(false) }
    var recurrenceType   by remember { mutableStateOf<String?>(null) }

    var hasTime          by remember { mutableStateOf(false) }
    var startTimeHour    by remember { mutableStateOf(8) }
    var startTimeMinute  by remember { mutableStateOf(0) }
    var endTimeHour      by remember { mutableStateOf(9) }
    var endTimeMinute    by remember { mutableStateOf(0) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker   by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Top bar ───────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            Spacer(Modifier.width(4.dp))
            Text(
                "NEW MISSION",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize   = 16.sp,
                fontFamily = PixelFontFamily
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Goal name field
            PixelTextField(value = title, onValueChange = { title = it }, placeholder = "GOAL NAME")

            // Start date
            PixelDateField(label = "START DATE", value = startDateText, onClick = { showStartPicker = true })

            // End date
            PixelDateField(label = "END DATE", value = endDateText, onClick = { showEndPicker = true })

            // Has Time toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { hasTime = !hasTime }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "SPECIFY TIME?",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 11.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
                Text(
                    if (hasTime) "[ON]" else "[OFF]",
                    color = if (hasTime) MaterialTheme.colorScheme.primary else Color.Gray,
                    fontSize   = 11.sp,
                    fontFamily = PixelFontFamily
                )
            }

            if (hasTime) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        PixelDateField(
                            label = "START TIME",
                            value = String.format(Locale.getDefault(), "%02d:%02d", startTimeHour, startTimeMinute),
                            onClick = { showStartTimePicker = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PixelDateField(
                            label = "END TIME",
                            value = String.format(Locale.getDefault(), "%02d:%02d", endTimeHour, endTimeMinute),
                            onClick = { showEndTimePicker = true }
                        )
                    }
                }
            }

            // Difficulty
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "DIFFICULTY",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 11.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    addDifficulties.forEach { diff ->
                        val sel = difficulty == diff.key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(2.dp, MaterialTheme.colorScheme.primary)
                                .background(if (sel) Color.White else Color.Black)
                                .clickable { difficulty = diff.key }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PixelDifficultyIcon(
                                    difficulty = diff.key,
                                    modifier   = Modifier.size(26.dp),
                                    color      = if (sel) Color.Black else Color.White
                                )
                                Text(
                                    diff.label,
                                    color      = if (sel) Color.Black else Color.White,
                                    fontSize   = 7.sp,
                                    fontFamily = PixelFontFamily,
                                    textAlign  = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                // XP hint
                val xpHint = when (difficulty) {
                    "Easy" -> 10; "Medium" -> 25; "Hard" -> 50; else -> 100
                }
                Text(
                    "+$xpHint XP ON COMPLETION",
                    color      = Color(0xFF555555),
                    fontSize   = 7.sp,
                    fontFamily = PixelFontFamily
                )
            }

            // Category
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "CHOOSE CATEGORY",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 11.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .horizontalScroll(rememberScrollState())
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Categories.all.forEach { cat ->
                        val sel = category == cat
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .background(if (sel) Color.White else Color.Black)
                                .border(if (sel) 0.dp else 1.dp, Color(0xFF444444))
                                .clickable { category = cat }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PixelCategoryIcon(
                                    category = cat,
                                    modifier = Modifier.size(28.dp),
                                    color    = if (sel) Color.Black else Color.White
                                )
                                Text(
                                    Categories.label(cat),
                                    color      = if (sel) Color.Black else Color.White,
                                    fontSize   = 7.sp,
                                    fontFamily = PixelFontFamily,
                                    textAlign  = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Recurrence ───────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "RECURRENCE",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 11.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(null to "ONCE", "daily" to "DAILY", "weekly" to "WEEKLY", "monthly" to "MONTHLY").forEach { (key, label) ->
                        val sel = recurrenceType == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(if (sel) 2.dp else 1.dp, if (sel) Color.White else Color(0xFF333333))
                                .background(if (sel) Color.White else Color.Black)
                                .clickable { recurrenceType = key }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color      = if (sel) Color.Black else Color(0xFF555555),
                                fontSize   = 6.sp,
                                fontFamily = PixelFontFamily
                            )
                        }
                    }
                }
                Text(
                    if (recurrenceType == null) "SINGLE GOAL — COMPLETE ONCE"
                    else "AUTO-RECREATES AFTER COMPLETION",
                    color      = Color(0xFF444444),
                    fontSize   = 7.sp,
                    fontFamily = PixelFontFamily
                )
            }

            // INITIATE button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        when {
                            title.isBlank() ->
                                Toast.makeText(context, "Enter a goal title", Toast.LENGTH_SHORT).show()
                            startDateMillis == null || endDateMillis == null ->
                                Toast.makeText(context, "Select start and end dates", Toast.LENGTH_SHORT).show()
                            endDateMillis!! < startDateMillis!! ->
                                Toast.makeText(context, "End date must be after or same as start", Toast.LENGTH_SHORT).show()
                            else -> {
                                var finalStartMillis = startDateMillis!!
                                var finalEndMillis = endDateMillis!!
                                if (hasTime) {
                                    val startCal = Calendar.getInstance().apply { timeInMillis = finalStartMillis }
                                    startCal.set(Calendar.HOUR_OF_DAY, startTimeHour)
                                    startCal.set(Calendar.MINUTE, startTimeMinute)
                                    finalStartMillis = startCal.timeInMillis

                                    val endCal = Calendar.getInstance().apply { timeInMillis = finalEndMillis }
                                    endCal.set(Calendar.HOUR_OF_DAY, endTimeHour)
                                    endCal.set(Calendar.MINUTE, endTimeMinute)
                                    finalEndMillis = endCal.timeInMillis
                                }

                                if (hasTime && finalEndMillis <= finalStartMillis) {
                                    Toast.makeText(context, "End time must be after start time", Toast.LENGTH_SHORT).show()
                                    return@clickable
                                }

                                viewModel.addGoal(
                                    Goal(
                                        title          = title.trim(),
                                        startDate      = finalStartMillis,
                                        endDate        = finalEndMillis,
                                        difficulty     = difficulty,
                                        category       = category,
                                        recurrenceType = recurrenceType,
                                        hasTime        = hasTime
                                    )
                                )
                                Toast.makeText(context, "Mission added!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "INITIATE",
                    color = MaterialTheme.colorScheme.background,
                    fontSize   = 16.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 3.sp
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }

    if (showStartPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        startDateMillis = it
                        startDateText   = fmt.format(Date(it))
                    }
                    showStartPicker = false
                }) { Text("OK", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 10.sp) }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) {
                    Text("CANCEL", color = Color(0xFF888888), fontFamily = PixelFontFamily, fontSize = 10.sp)
                }
            }
        ) { DatePicker(state = state) }
    }

    if (showEndPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        endDateMillis = it
                        endDateText   = fmt.format(Date(it))
                    }
                    showEndPicker = false
                }) { Text("OK", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 10.sp) }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text("CANCEL", color = Color(0xFF888888), fontFamily = PixelFontFamily, fontSize = 10.sp)
                }
            }
        ) { DatePicker(state = state) }
    }

    if (showStartTimePicker) {
        val state = rememberTimePickerState(initialHour = startTimeHour, initialMinute = startTimeMinute)
        AlertDialog(
            onDismissRequest = { showStartTimePicker = false },
            containerColor = MaterialTheme.colorScheme.background,
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    startTimeHour = state.hour
                    startTimeMinute = state.minute
                    showStartTimePicker = false
                }) { Text("OK", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily) }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) {
                    Text("CANCEL", color = Color.Gray, fontFamily = PixelFontFamily)
                }
            }
        )
    }

    if (showEndTimePicker) {
        val state = rememberTimePickerState(initialHour = endTimeHour, initialMinute = endTimeMinute)
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            containerColor = MaterialTheme.colorScheme.background,
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(onClick = {
                    endTimeHour = state.hour
                    endTimeMinute = state.minute
                    showEndTimePicker = false
                }) { Text("OK", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily) }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) {
                    Text("CANCEL", color = Color.Gray, fontFamily = PixelFontFamily)
                }
            }
        )
    }
}

// ── Shared pixel input components ─────────────────────────────────

@Composable
fun PixelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true
) {
    BasicTextField(
        value         = value,
        onValueChange = onValueChange,
        textStyle     = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = PixelFontFamily,
            fontSize   = 13.sp
        ),
        cursorBrush = SolidColor(Color.White),
        singleLine  = singleLine,
        decorationBox = { inner ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        placeholder,
                        color      = Color(0xFF444444),
                        fontFamily = PixelFontFamily,
                        fontSize   = 13.sp
                    )
                }
                inner()
            }
        }
    )
}

@Composable
fun PixelDateField(label: String, value: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            label,
            color      = Color(0xFF777777),
            fontSize   = 8.sp,
            fontFamily = PixelFontFamily,
            letterSpacing = 1.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .background(MaterialTheme.colorScheme.background)
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    value.ifBlank { "SELECT DATE" },
                    color      = if (value.isBlank()) Color(0xFF444444) else Color.White,
                    fontFamily = PixelFontFamily,
                    fontSize   = 11.sp
                )
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint     = Color(0xFF666666),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}