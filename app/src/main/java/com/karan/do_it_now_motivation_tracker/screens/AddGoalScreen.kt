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
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Easy") }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

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
            value = startDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Start Date") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showStartPicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = endDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("End Date") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showEndPicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Difficulty")

        Spacer(modifier = Modifier.height(10.dp))

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
                        "Please enter goal title",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                val goal = Goal(
                    id = viewModel.goals.size + 1,
                    title = title,
                    startDate = startDate,
                    endDate = endDate,
                    difficulty = difficulty
                )

                viewModel.addGoal(goal)

                Toast.makeText(
                    context,
                    "Goal added successfully 🎉",
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

        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {

                TextButton(onClick = {

                    val millis = datePickerState.selectedDateMillis

                    if (millis != null) {

                        val formatter = SimpleDateFormat(
                            "dd MMM yyyy",
                            Locale.getDefault()
                        )

                        startDate = formatter.format(Date(millis))
                    }

                    showStartPicker = false

                }) {
                    Text("OK")
                }
            }
        ) {

            DatePicker(state = datePickerState)

        }
    }

    if (showEndPicker) {

        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {

                TextButton(onClick = {

                    val millis = datePickerState.selectedDateMillis

                    if (millis != null) {

                        val formatter = SimpleDateFormat(
                            "dd MMM yyyy",
                            Locale.getDefault()
                        )

                        endDate = formatter.format(Date(millis))
                    }

                    showEndPicker = false

                }) {
                    Text("OK")
                }
            }
        ) {

            DatePicker(state = datePickerState)

        }
    }
}