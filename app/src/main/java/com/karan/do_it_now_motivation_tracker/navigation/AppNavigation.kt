package com.karan.do_it_now_motivation_tracker.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.karan.do_it_now_motivation_tracker.screens.AddGoalScreen
import com.karan.do_it_now_motivation_tracker.screens.DashboardScreen
import com.karan.do_it_now_motivation_tracker.screens.EditGoalScreen
import com.karan.do_it_now_motivation_tracker.screens.ProfileScreen
import com.karan.do_it_now_motivation_tracker.screens.SplashScreen
import com.karan.do_it_now_motivation_tracker.ui.theme.AtmosphericBlack
import com.karan.do_it_now_motivation_tracker.ui.theme.CardBorder
import com.karan.do_it_now_motivation_tracker.ui.theme.CyanGlow
import com.karan.do_it_now_motivation_tracker.ui.theme.DarkBlueSurface
import com.karan.do_it_now_motivation_tracker.ui.theme.DeepNavy
import com.karan.do_it_now_motivation_tracker.ui.theme.DimText
import com.karan.do_it_now_motivation_tracker.ui.theme.MutedPurple
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftIndigo
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val goalViewModel: GoalViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in listOf("dashboard", "profile")

    Scaffold(
        containerColor = AtmosphericBlack,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(tween(400)) { it } + fadeIn(tween(400)),
                exit = slideOutVertically(tween(300)) { it } + fadeOut(tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(DeepNavy.copy(alpha = 0.95f), AtmosphericBlack.copy(alpha = 0.98f))
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    CyanGlow.copy(alpha = 0.08f),
                                    SoftIndigo.copy(alpha = 0.12f),
                                    CyanGlow.copy(alpha = 0.08f)
                                )
                            ),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(70.dp)
                    ) {
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (currentRoute == "dashboard") Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text("home", fontSize = 10.sp, letterSpacing = 1.sp)
                            },
                            selected = currentRoute == "dashboard",
                            onClick = {
                                if (currentRoute != "dashboard") {
                                    navController.navigate("dashboard") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanGlow,
                                selectedTextColor = CyanGlow.copy(alpha = 0.7f),
                                unselectedIconColor = DimText,
                                unselectedTextColor = DimText,
                                indicatorColor = CyanGlow.copy(alpha = 0.08f)
                            )
                        )

                        // Spacer for center FAB
                        NavigationBarItem(
                            icon = {}, label = {}, selected = false,
                            onClick = {}, enabled = false
                        )

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (currentRoute == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text("profile", fontSize = 10.sp, letterSpacing = 1.sp)
                            },
                            selected = currentRoute == "profile",
                            onClick = {
                                if (currentRoute != "profile") {
                                    navController.navigate("profile") {
                                        popUpTo("dashboard")
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MutedPurple,
                                selectedTextColor = MutedPurple.copy(alpha = 0.7f),
                                unselectedIconColor = DimText,
                                unselectedTextColor = DimText,
                                indicatorColor = MutedPurple.copy(alpha = 0.08f)
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = { navController.navigate("addGoal") },
                    containerColor = Color.Transparent,
                    contentColor = CyanGlow,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(54.dp)
                        .border(
                            1.dp,
                            Brush.sweepGradient(
                                listOf(CyanGlow.copy(alpha = 0.5f), SoftIndigo.copy(alpha = 0.3f), CyanGlow.copy(alpha = 0.5f))
                            ),
                            CircleShape
                        )
                        .background(
                            Brush.radialGradient(listOf(DarkBlueSurface, AtmosphericBlack)),
                            CircleShape
                        )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Goal", modifier = Modifier.size(22.dp))
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AtmosphericBlack)
        ) {
            NavHost(
                navController = navController,
                startDestination = "splash",
                enterTransition = { fadeIn(tween(500)) },
                exitTransition = { fadeOut(tween(300)) }
            ) {
                composable("splash") {
                    SplashScreen(onFinished = {
                        navController.navigate("dashboard") {
                            popUpTo("splash") { inclusive = true }
                        }
                    })
                }

                composable("dashboard") {
                    DashboardScreen(navController, goalViewModel)
                }

                composable("addGoal") {
                    AddGoalScreen(navController, goalViewModel)
                }

                composable(
                    "editGoal/{goalId}",
                    arguments = listOf(navArgument("goalId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val goalId = backStackEntry.arguments?.getInt("goalId") ?: 0
                    EditGoalScreen(navController, goalViewModel, goalId)
                }

                composable("profile") {
                    ProfileScreen(goalViewModel)
                }
            }
        }
    }
}