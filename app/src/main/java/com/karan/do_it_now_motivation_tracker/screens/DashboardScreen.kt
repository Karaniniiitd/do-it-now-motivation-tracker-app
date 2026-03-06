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

data class Goal(
    val title: String,
    val progress: String
)

@Composable
fun DashboardScreen(navController: NavController) {

    val goals = listOf(
        Goal("Learn Android Development", "Day 3 / 10"),
        Goal("Gym Workout", "Day 7 / 30")
    )

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
                        Text(goal.progress)
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