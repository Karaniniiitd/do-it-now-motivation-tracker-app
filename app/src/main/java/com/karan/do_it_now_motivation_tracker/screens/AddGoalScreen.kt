package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AddGoalScreen(navController: NavController) {

    var goalTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Create Goal",
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = goalTitle,
            onValueChange = { goalTitle = it },
            label = { Text("Goal Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Start Date") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("End Date") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Difficulty")

        Spacer(modifier = Modifier.height(8.dp))

        Row {

            AssistChip(
                onClick = { },
                label = { Text("Easy") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            AssistChip(
                onClick = { },
                label = { Text("Medium") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            AssistChip(
                onClick = { },
                label = { Text("Hard") }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                navController.navigate("dashboard")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Goal")
        }
    }
}