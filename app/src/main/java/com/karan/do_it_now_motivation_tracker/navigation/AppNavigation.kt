package com.karan.do_it_now_motivation_tracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.karan.do_it_now_motivation_tracker.screens.LoginScreen
import com.karan.do_it_now_motivation_tracker.screens.DashboardScreen
import com.karan.do_it_now_motivation_tracker.screens.AddGoalScreen
import com.karan.do_it_now_motivation_tracker.screens.ProfileScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("addGoal") {
            AddGoalScreen(navController)
        }

        composable("profile") {
            ProfileScreen(navController)
        }
    }
}