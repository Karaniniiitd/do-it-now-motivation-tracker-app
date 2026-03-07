package com.karan.do_it_now_motivation_tracker.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    navController: NavController,
    viewModel: GoalViewModel
) {

    val context = LocalContext.current

    var title by remember { mutableStateOf("") }

    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }

    var startDateText by remember { mutableStateOf("") }
    var endDateText by remember { mutableStateOf("") }

    var difficulty by remember { mutableStateOf("Easy") }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Create Goal",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Goal Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = startDateText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Start Date") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showStartPicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = endDateText,
            onValueChange = {},
            readOnly = true,
            label = { Text("End Date") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showEndPicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Difficulty")

        Spacer(modifier = Modifier.height(8.dp))

        Row {

            FilterChip(
                selected = difficulty == "Easy",
                onClick = { difficulty = "Easy" },
                label = { Text("Easy") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                selected = difficulty == "Medium",
                onClick = { difficulty = "Medium" },
                label = { Text("Medium") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                selected = difficulty == "Hard",
                onClick = { difficulty = "Hard" },
                label = { Text("Hard") }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                if (title.isBlank()) {

                    Toast.makeText(
                        context,
                        "Enter goal title",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                if (startDateMillis == null || endDateMillis == null) {

                    Toast.makeText(
                        context,
                        "Select start and end date",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                val goal = Goal(
                    id = viewModel.goals.size + 1,
                    title = title,
                    startDate = startDateMillis!!,
                    endDate = endDateMillis!!,
                    difficulty = difficulty
                )

                viewModel.addGoal(goal)

                Toast.makeText(
                    context,
                    "Goal added 🎉",
                    Toast.LENGTH_SHORT
                ).show()

                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Goal")
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
                        startDateText = formatter.format(Date(it))
                    }

                    showStartPicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showEndPicker) {

        val state = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {

                TextButton(onClick = {

                    state.selectedDateMillis?.let {

                        endDateMillis = it
                        endDateText = formatter.format(Date(it))
                    }

                    showEndPicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}