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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.ui.components.Categories
import com.karan.do_it_now_motivation_tracker.ui.components.ParticleOverlay
import com.karan.do_it_now_motivation_tracker.ui.components.PixelCategoryIcon
import com.karan.do_it_now_motivation_tracker.ui.components.PixelDifficultyIcon
import com.karan.do_it_now_motivation_tracker.ui.components.burstParticles
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.AlertDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalScreen(navController: NavController, viewModel: GoalViewModel, goalId: Int) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var goal            by remember { mutableStateOf<Goal?>(null) }
    var title           by remember { mutableStateOf("") }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis   by remember { mutableStateOf<Long?>(null) }
    var startDateText   by remember { mutableStateOf("") }
    var endDateText     by remember { mutableStateOf("") }
    var difficulty      by remember { mutableStateOf("Easy") }
    var category        by remember { mutableStateOf(Categories.all.first()) }
    var progressValue   by remember { mutableStateOf(0) }
    var notes           by remember { mutableStateOf("") }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker   by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoaded        by remember { mutableStateOf(false) }

    var hasTime         by remember { mutableStateOf(false) }
    var startTimeHour   by remember { mutableStateOf(8) }
    var startTimeMinute by remember { mutableStateOf(0) }
    var endTimeHour     by remember { mutableStateOf(9) }
    var endTimeMinute   by remember { mutableStateOf(0) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker   by remember { mutableStateOf(false) }

    // Particle system
    val particles = remember { mutableStateListOf<com.karan.do_it_now_motivation_tracker.ui.components.Particle>() }
    var boxWidthPx  by remember { mutableStateOf(0f) }
    var boxHeightPx by remember { mutableStateOf(0f) }

    LaunchedEffect(goalId) {
        val loaded = viewModel.getGoalById(goalId)
        if (loaded != null) {
            goal            = loaded
            title           = loaded.title
            startDateMillis = loaded.startDate
            endDateMillis   = loaded.endDate
            startDateText   = fmt.format(Date(loaded.startDate))
            endDateText     = fmt.format(Date(loaded.endDate))
            difficulty      = loaded.difficulty
            category        = loaded.category
            progressValue   = loaded.progressPercent
            notes           = loaded.notes
            hasTime         = loaded.hasTime
            
            if (loaded.hasTime) {
                val startCal = Calendar.getInstance().apply { timeInMillis = loaded.startDate }
                startTimeHour = startCal.get(Calendar.HOUR_OF_DAY)
                startTimeMinute = startCal.get(Calendar.MINUTE)
                
                val endCal = Calendar.getInstance().apply { timeInMillis = loaded.endDate }
                endTimeHour = endCal.get(Calendar.HOUR_OF_DAY)
                endTimeMinute = endCal.get(Calendar.MINUTE)
            }

            isLoaded        = true
        } else {
            Toast.makeText(context, "Goal not found", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    if (!isLoaded) return

    if (showUpdateDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            containerColor   = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "UPDATE MISSION",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize = 13.sp
                )
            },
            text = {
                Text(
                    "ARE YOU SURE YOU WANT TO SAVE THESE CHANGES?",
                    color = Color(0xFF888888),
                    fontFamily = PixelFontFamily,
                    fontSize = 9.sp,
                    lineHeight = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showUpdateDialog = false
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

                    goal?.let {
                        viewModel.updateGoal(
                            it.copy(
                                title           = title.trim(),
                                startDate       = finalStartMillis,
                                endDate         = finalEndMillis,
                                difficulty      = difficulty,
                                category        = category,
                                progressPercent = progressValue,
                                notes           = notes,
                                hasTime         = hasTime
                            )
                        )
                    }
                    Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }) {
                    Text("CONFIRM", color = MaterialTheme.colorScheme.primary, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateDialog = false }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor   = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "DELETE MISSION",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize = 13.sp
                )
            },
            text = {
                Text(
                    "ARE YOU SURE YOU WANT TO DELETE THIS MISSION? THIS ACTION CANNOT BE UNDONE.",
                    color = Color(0xFF888888),
                    fontFamily = PixelFontFamily,
                    fontSize = 9.sp,
                    lineHeight = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    goal?.let { viewModel.deleteGoal(it) }
                    Toast.makeText(context, "Mission deleted", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }) {
                    Text("DELETE", color = Color(0xFFFF4444), fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned { coords ->
                boxWidthPx  = coords.size.width.toFloat()
                boxHeightPx = coords.size.height.toFloat()
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top bar ───────────────────────────────────────────
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
                    "EDIT MISSION",
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
                // Title
                PixelTextField(value = title, onValueChange = { title = it }, placeholder = "GOAL NAME")

                // Dates
                PixelDateField(label = "START DATE", value = startDateText, onClick = { showStartPicker = true })
                PixelDateField(label = "END DATE",   value = endDateText,   onClick = { showEndPicker   = true })

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

                // ── PROGRESS SLIDER ───────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "PROGRESS",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize   = 11.sp,
                            fontFamily = PixelFontFamily,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "$progressValue%",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize   = 14.sp,
                            fontFamily = PixelFontFamily
                        )
                    }
                    // 20-segment pixel progress bar (tappable segments)
                    val segments = 20
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        repeat(segments) { i ->
                            val threshold = (i + 1) * (100 / segments)
                            val filled    = progressValue >= threshold
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(22.dp)
                                    .border(1.dp, if (filled) Color.White else Color(0xFF333333))
                                    .background(if (filled) Color.White else Color.Black)
                                    .clickable {
                                        progressValue = threshold
                                        viewModel.updateProgressOnly(goalId, threshold)
                                    }
                            )
                        }
                    }
                    Text(
                        "TAP SEGMENT TO SET PROGRESS",
                        color      = Color(0xFF555555),
                        fontSize   = 7.sp,
                        fontFamily = PixelFontFamily
                    )
                }

                // ── NOTES FIELD ───────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "MISSION NOTES",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize   = 11.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 1.sp
                    )
                    PixelTextField(
                        value         = notes,
                        onValueChange = { notes = it },
                        placeholder   = "LOG YOUR PROGRESS, BLOCKERS, WINS...",
                        singleLine    = false
                    )
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
                        listOf("Easy", "Medium", "Hard", "Boss").forEach { diff ->
                            val sel = difficulty == diff
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(2.dp, MaterialTheme.colorScheme.primary)
                                    .background(if (sel) Color.White else Color.Black)
                                    .clickable { difficulty = diff }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PixelDifficultyIcon(
                                        difficulty = diff,
                                        modifier   = Modifier.size(26.dp),
                                        color      = if (sel) Color.Black else Color.White
                                    )
                                    Text(
                                        diff.uppercase(),
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

                // Category
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "CATEGORY",
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

                // Save
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                        when {
                            title.isBlank() ->
                                Toast.makeText(context, "Enter a title", Toast.LENGTH_SHORT).show()
                            startDateMillis == null || endDateMillis == null ->
                                Toast.makeText(context, "Select dates", Toast.LENGTH_SHORT).show()
                            endDateMillis!! < startDateMillis!! ->
                                Toast.makeText(context, "End must be after or same as start", Toast.LENGTH_SHORT).show()
                            else -> {
                                var valid = true
                                if (hasTime) {
                                    var finalStartMillis = startDateMillis!!
                                    var finalEndMillis = endDateMillis!!
                                    val startCal = Calendar.getInstance().apply { timeInMillis = finalStartMillis }
                                    startCal.set(Calendar.HOUR_OF_DAY, startTimeHour)
                                    startCal.set(Calendar.MINUTE, startTimeMinute)
                                    finalStartMillis = startCal.timeInMillis

                                    val endCal = Calendar.getInstance().apply { timeInMillis = finalEndMillis }
                                    endCal.set(Calendar.HOUR_OF_DAY, endTimeHour)
                                    endCal.set(Calendar.MINUTE, endTimeMinute)
                                    finalEndMillis = endCal.timeInMillis

                                    if (finalEndMillis <= finalStartMillis) {
                                        Toast.makeText(context, "End time must be after start time", Toast.LENGTH_SHORT).show()
                                        valid = false
                                    }
                                }
                                if (valid) {
                                    showUpdateDialog = true
                                }
                            }
                        }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "SAVE CHANGES",
                        color = MaterialTheme.colorScheme.background,
                        fontSize   = 15.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 2.sp
                    )
                }

                // Complete / uncomplete with PARTICLE BURST
                val isCompleted = goal?.isCompleted == true
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .background(MaterialTheme.colorScheme.background)
                        .clickable {
                            goal?.let {
                                if (it.isCompleted) {
                                    viewModel.uncompleteGoal(it)
                                    Toast.makeText(context, "Marked incomplete", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.completeGoal(it)
                                    // Fire particle burst from center of screen
                                    particles.addAll(
                                        burstParticles(boxWidthPx / 2f, boxHeightPx / 2f, count = 60)
                                    )
                                    Toast.makeText(context, "MISSION COMPLETE! +XP", Toast.LENGTH_SHORT).show()
                                }
                                navController.popBackStack()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isCompleted) "MARK INCOMPLETE" else "COMPLETE MISSION",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize   = 13.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 1.sp
                    )
                }

                // Delete
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, Color(0xFF444444))
                        .background(MaterialTheme.colorScheme.background)
                        .clickable {
                            showDeleteDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "DELETE MISSION",
                        color      = Color(0xFF555555),
                        fontSize   = 11.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(80.dp))
            }
        }

        // Particle overlay on top of everything
        ParticleOverlay(particles = particles, modifier = Modifier.fillMaxSize())
    }

    // Date pickers
    if (showStartPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        startDateMillis = it; startDateText = fmt.format(Date(it))
                    }; showStartPicker = false
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
        val state = rememberDatePickerState(initialSelectedDateMillis = endDateMillis)
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        endDateMillis = it; endDateText = fmt.format(Date(it))
                    }; showEndPicker = false
                }) { Text("OK", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 10.sp) }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) {
                    Text("CANCEL", color = Color(0xFF888888), fontFamily = PixelFontFamily, fontSize = 10.sp)
                }
            }
        ) { DatePicker(state = state) }
    }
}
