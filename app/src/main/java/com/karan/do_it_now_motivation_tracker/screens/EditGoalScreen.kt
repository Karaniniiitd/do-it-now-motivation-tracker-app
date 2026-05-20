package com.karan.do_it_now_motivation_tracker.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.ui.theme.AtmosphericBlack
import com.karan.do_it_now_motivation_tracker.ui.theme.CardBorder
import com.karan.do_it_now_motivation_tracker.ui.theme.CyanGlow
import com.karan.do_it_now_motivation_tracker.ui.theme.MidnightCard
import com.karan.do_it_now_motivation_tracker.ui.theme.MutedText
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftGreen
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftRed
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftWhite
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftYellow
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalScreen(navController: NavController, viewModel: GoalViewModel, goalId: Int) {
    val context = LocalContext.current
    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    var goal by remember { mutableStateOf<Goal?>(null) }
    var title by remember { mutableStateOf("") }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Easy") }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(goalId) {
        val loaded = viewModel.getGoalById(goalId)
        if (loaded != null) {
            goal = loaded
            title = loaded.title
            startDateMillis = loaded.startDate
            endDateMillis = loaded.endDate
            startDateText = fmt.format(Date(loaded.startDate))
            endDateText = fmt.format(Date(loaded.endDate))
            difficulty = loaded.difficulty
            isLoaded = true
        } else {
            Toast.makeText(context, "goal not found", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    val tfColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = CyanGlow.copy(alpha = 0.5f),
        unfocusedBorderColor = CardBorder.copy(alpha = 0.3f),
        cursorColor = CyanGlow,
        focusedLabelColor = CyanGlow.copy(alpha = 0.7f),
        unfocusedLabelColor = MutedText.copy(alpha = 0.5f),
        focusedTextColor = SoftWhite,
        unfocusedTextColor = SoftWhite.copy(alpha = 0.8f),
        focusedContainerColor = MidnightCard.copy(alpha = 0.3f),
        unfocusedContainerColor = MidnightCard.copy(alpha = 0.2f)
    )

    if (!isLoaded) return

    Column(modifier = Modifier.fillMaxSize().background(AtmosphericBlack)) {
        TopAppBar(
            title = { Text("edit goal", color = SoftWhite, letterSpacing = 1.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SoftWhite.copy(alpha = 0.7f))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("goal title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = tfColors,
                singleLine = true
            )

            OutlinedTextField(
                value = startDateText,
                onValueChange = {},
                readOnly = true,
                label = { Text("start date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = tfColors,
                trailingIcon = {
                    IconButton(onClick = { showStartPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = CyanGlow.copy(alpha = 0.5f))
                    }
                }
            )

            OutlinedTextField(
                value = endDateText,
                onValueChange = {},
                readOnly = true,
                label = { Text("end date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = tfColors,
                trailingIcon = {
                    IconButton(onClick = { showEndPicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = CyanGlow.copy(alpha = 0.5f))
                    }
                }
            )

            Text("difficulty", style = MaterialTheme.typography.titleMedium, color = MutedText, letterSpacing = 1.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Easy" to SoftGreen, "Medium" to SoftYellow, "Hard" to SoftRed).forEach { (lbl, clr) ->
                    FilterChip(
                        selected = difficulty == lbl,
                        onClick = { difficulty = lbl },
                        label = { Text(lbl.lowercase()) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = clr.copy(alpha = 0.1f),
                            selectedLabelColor = clr,
                            containerColor = MidnightCard.copy(alpha = 0.3f),
                            labelColor = MutedText.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = difficulty == lbl,
                            borderColor = CardBorder.copy(alpha = 0.2f),
                            selectedBorderColor = clr.copy(alpha = 0.2f)
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "enter title", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (startDateMillis == null || endDateMillis == null) {
                        Toast.makeText(context, "select dates", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (endDateMillis!! <= startDateMillis!!) {
                        Toast.makeText(context, "end must be after start", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    goal?.let {
                        viewModel.updateGoal(
                            it.copy(
                                title = title,
                                startDate = startDateMillis!!,
                                endDate = endDateMillis!!,
                                difficulty = difficulty
                            )
                        )
                    }
                    Toast.makeText(context, "updated ✦", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanGlow.copy(alpha = 0.15f),
                    contentColor = CyanGlow
                )
            ) {
                Text("save changes", style = MaterialTheme.typography.titleMedium, letterSpacing = 1.sp)
            }

            OutlinedButton(
                onClick = {
                    goal?.let { viewModel.deleteGoal(it) }
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftRed.copy(alpha = 0.7f))
            ) {
                Text("delete goal", style = MaterialTheme.typography.titleMedium, letterSpacing = 1.sp)
            }

            Spacer(Modifier.height(80.dp))
        }
    }

    if (showStartPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { startDateMillis = it; startDateText = fmt.format(Date(it)) }
                    showStartPicker = false
                }) { Text("ok", color = CyanGlow) }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("cancel", color = MutedText) }
            }
        ) { DatePicker(state = state) }
    }

    if (showEndPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = endDateMillis)
        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { endDateMillis = it; endDateText = fmt.format(Date(it)) }
                    showEndPicker = false
                }) { Text("ok", color = CyanGlow) }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("cancel", color = MutedText) }
            }
        ) { DatePicker(state = state) }
    }
}
