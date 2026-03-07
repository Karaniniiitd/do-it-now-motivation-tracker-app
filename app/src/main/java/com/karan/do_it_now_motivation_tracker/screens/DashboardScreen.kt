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

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: GoalViewModel
) {

    val goals = viewModel.goals

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Good Evening 👋",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("🔥 Current Streak")
                Text("5 Days", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Your Goals", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {

            items(goals) { goal ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(goal.title)

                        Text(
                            "${goal.startDate} → ${goal.endDate}"
                        )

                        Text(
                            "Difficulty: ${goal.difficulty}"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("addGoal")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ Add New Goal")
        }
    }
}