package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.*

fun calculateProgress(start: Long, end: Long): Float {

    val now = System.currentTimeMillis()

    if (end <= start) return 0f

    val total = end - start
    val elapsed = now - start

    val progress = elapsed.toFloat() / total.toFloat()

    return progress.coerceIn(0f, 1f)
}

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: GoalViewModel
) {

    val goals = viewModel.goals

    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Scaffold(

        floatingActionButton = {

            FloatingActionButton(
                onClick = { navController.navigate("addGoal") }
            ) {
                Text("+")
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "Good Evening 👋",
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text("🔥 Current Streak")

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "5 Days",
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your Goals",
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {

                items(goals) { goal ->

                    val progress = calculateProgress(
                        goal.startDate,
                        goal.endDate
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = goal.title,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text =
                                    "${formatter.format(Date(goal.startDate))} → " +
                                            formatter.format(Date(goal.endDate))
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${(progress * 100).toInt()}% completed"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Difficulty: ${goal.difficulty}"
                            )
                        }
                    }
                }
            }
        }
    }
}