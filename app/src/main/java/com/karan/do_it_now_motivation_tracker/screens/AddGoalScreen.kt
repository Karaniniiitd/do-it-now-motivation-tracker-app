package com.karan.do_it_now_motivation_tracker.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.ui.components.Categories
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

// Split categories into two rows of 4 for stable layout (no FlowRow)
private val catRow1 = listOf("General", "Health", "Study", "Work")
private val catRow2 = listOf("Fitness", "Finance", "Personal", "Creative")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(navController: NavController, viewModel: GoalViewModel) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }
    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Easy") }
    var category by remember { mutableStateOf("General") }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

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

    Column(Modifier.fillMaxSize().background(AtmosphericBlack)) {
        TopAppBar(
            title = { Text("Create Goal", color = SoftWhite) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SoftWhite.copy(alpha = 0.7f))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Goal title") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp), colors = tfColors, singleLine = true
            )

            // Start date
            OutlinedTextField(
                value = startDateText, onValueChange = {}, readOnly = true,
                label = { Text("Start date") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp), colors = tfColors,
                trailingIcon = {
                    IconButton(onClick = { showStartPicker = true }) {
                        Icon(Icons.Default.DateRange, null, tint = CyanGlow.copy(alpha = 0.5f))
                    }
                }
            )

            // End date
            OutlinedTextField(
                value = endDateText, onValueChange = {}, readOnly = true,
                label = { Text("End date") }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp), colors = tfColors,
                trailingIcon = {
                    IconButton(onClick = { showEndPicker = true }) {
                        Icon(Icons.Default.DateRange, null, tint = CyanGlow.copy(alpha = 0.5f))
                    }
                }
            )

            // Category
            Text("Category", style = MaterialTheme.typography.titleMedium, color = MutedText)
            CategoryChipRow(catRow1, category) { category = it }
            CategoryChipRow(catRow2, category) { category = it }

            // Difficulty
            Text("Difficulty", style = MaterialTheme.typography.titleMedium, color = MutedText)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Easy" to SoftGreen, "Medium" to SoftYellow, "Hard" to SoftRed).forEach { (lbl, clr) ->
                    val sel = difficulty == lbl
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (sel) clr.copy(alpha = 0.15f) else MidnightCard.copy(alpha = 0.3f))
                            .border(1.dp, if (sel) clr.copy(alpha = 0.3f) else CardBorder.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable { difficulty = lbl }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(lbl, style = MaterialTheme.typography.labelLarge, color = if (sel) clr else MutedText)
                    }
                }
            }

            // XP hint
            val xpPreview = when (difficulty) { "Easy" -> 10; "Medium" -> 25; else -> 50 }
            Text(
                "+$xpPreview XP on completion",
                style = MaterialTheme.typography.bodySmall,
                color = CyanGlow.copy(alpha = 0.55f),
                letterSpacing = 0.5.sp
            )

            Spacer(Modifier.height(4.dp))

            // Create button
            Button(
                onClick = {
                    when {
                        title.isBlank() ->
                            Toast.makeText(context, "Enter a goal title", Toast.LENGTH_SHORT).show()
                        startDateMillis == null || endDateMillis == null ->
                            Toast.makeText(context, "Select start and end dates", Toast.LENGTH_SHORT).show()
                        endDateMillis!! <= startDateMillis!! ->
                            Toast.makeText(context, "End date must be after start date", Toast.LENGTH_SHORT).show()
                        else -> {
                            viewModel.addGoal(
                                Goal(
                                    title = title.trim(),
                                    startDate = startDateMillis!!,
                                    endDate = endDateMillis!!,
                                    difficulty = difficulty,
                                    category = category
                                )
                            )
                            Toast.makeText(context, "Goal created ✦", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanGlow.copy(alpha = 0.18f),
                    contentColor = CyanGlow
                )
            ) {
                Text("Create Goal", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(90.dp))
        }
    }

    // Date pickers
    if (showStartPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        startDateMillis = it
                        startDateText = fmt.format(Date(it))
                    }
                    showStartPicker = false
                }) { Text("OK", color = CyanGlow) }
            },
            dismissButton = {
                TextButton(onClick = { showStartPicker = false }) { Text("Cancel", color = MutedText) }
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
                        endDateText = fmt.format(Date(it))
                    }
                    showEndPicker = false
                }) { Text("OK", color = CyanGlow) }
            },
            dismissButton = {
                TextButton(onClick = { showEndPicker = false }) { Text("Cancel", color = MutedText) }
            }
        ) { DatePicker(state = state) }
    }
}

@Composable
private fun CategoryChipRow(cats: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cats.forEach { cat ->
            val clr = Categories.color(cat)
            val sel = selected == cat
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (sel) clr.copy(alpha = 0.15f) else MidnightCard.copy(alpha = 0.3f))
                    .border(1.dp, if (sel) clr.copy(alpha = 0.35f) else CardBorder.copy(alpha = 0.2f), RoundedCornerShape(11.dp))
                    .clickable { onSelect(cat) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Categories.emoji(cat), fontSize = 16.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        cat,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (sel) clr else MutedText
                    )
                }
            }
        }
    }
}