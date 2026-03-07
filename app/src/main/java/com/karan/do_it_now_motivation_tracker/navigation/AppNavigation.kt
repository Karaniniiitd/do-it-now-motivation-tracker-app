package com.karan.do_it_now_motivation_tracker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.karan.do_it_now_motivation_tracker.screens.*
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val goalViewModel: GoalViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("dashboard") {
            DashboardScreen(navController, goalViewModel)
        }

        composable("addGoal") {
            AddGoalScreen(navController, goalViewModel)
        }

        composable("profile") {
            ProfileScreen(navController)
        }
    }
}