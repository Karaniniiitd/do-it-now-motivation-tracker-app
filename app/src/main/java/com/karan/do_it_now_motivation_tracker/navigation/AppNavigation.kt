package com.karan.do_it_now_motivation_tracker.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.karan.do_it_now_motivation_tracker.screens.CategoryAnalyticsScreen
import com.karan.do_it_now_motivation_tracker.screens.CharacterScreen
import com.karan.do_it_now_motivation_tracker.screens.AiChatScreen
import com.karan.do_it_now_motivation_tracker.screens.DashboardScreen
import com.karan.do_it_now_motivation_tracker.screens.EditGoalScreen
import com.karan.do_it_now_motivation_tracker.screens.GoalListScreen
import com.karan.do_it_now_motivation_tracker.screens.LeaderboardScreen
import com.karan.do_it_now_motivation_tracker.screens.LoginScreen
import com.karan.do_it_now_motivation_tracker.screens.OnboardingScreen
import com.karan.do_it_now_motivation_tracker.screens.ProfileScreen
import com.karan.do_it_now_motivation_tracker.screens.SplashScreen
import com.karan.do_it_now_motivation_tracker.screens.WeeklyReportScreen
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconBarChartNav
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconClipboard
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconGear
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconHome
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconPlus
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.util.FirebaseManager
import com.karan.do_it_now_motivation_tracker.util.UserPrefsManager
import com.karan.do_it_now_motivation_tracker.viewmodel.AiChatViewModel
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel

private val bottomBarRoutes = setOf("dashboard", "goals", "profile", "analytics", "character", "weeklyReport")

@Composable
fun AppNavigation() {
    val navController  = rememberNavController()
    val goalViewModel: GoalViewModel = viewModel()
    val context        = LocalContext.current
    val db             = remember { com.karan.do_it_now_motivation_tracker.data.AppDatabase.getDatabase(context) }
    val aiViewModel: AiChatViewModel = viewModel(factory = com.karan.do_it_now_motivation_tracker.viewmodel.AiChatViewModelFactory(db.aiChatDao()))
    val prefs          = remember { UserPrefsManager.getInstance(context) }

    // Ensure weekly report generated on app launch
    androidx.compose.runtime.LaunchedEffect(Unit) {
        goalViewModel.ensureWeeklyReport()
    }

    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            if (showBottomBar) {
                PixelBottomNav(
                    currentRoute = currentRoute,
                    onNavClick = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    },
                    onAddClick = { navController.navigate("addGoal") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black)
        ) {
            NavHost(
                navController    = navController,
                startDestination = "splash",
                enterTransition  = { fadeIn(tween(300)) },
                exitTransition   = { fadeOut(tween(200)) }
            ) {
                // Splash — decides next screen
                composable("splash") {
                    SplashScreen(onFinished = {
                        val dest = when {
                            !prefs.hasOnboarded          -> "login"
                            else                          -> "dashboard"
                        }
                        navController.navigate(dest) {
                            popUpTo("splash") { inclusive = true }
                        }
                    })
                }

                // Onboarding
                composable("onboarding") {
                    OnboardingScreen(navController)
                }

                // Dashboard
                composable("dashboard") {
                    DashboardScreen(navController, goalViewModel)
                }

                // Goals list
                composable("goals") {
                    GoalListScreen(navController, goalViewModel)
                }

                // Add goal
                composable("addGoal") {
                    AddGoalScreen(navController, goalViewModel)
                }

                // Edit goal
                composable(
                    "editGoal/{goalId}",
                    arguments = listOf(navArgument("goalId") { type = NavType.IntType })
                ) { back ->
                    val goalId = back.arguments?.getInt("goalId") ?: 0
                    EditGoalScreen(navController, goalViewModel, goalId)
                }

                // Profile / Stats
                composable("profile") {
                    ProfileScreen(navController, goalViewModel)
                }

                // Settings
                composable("settings") {
                    com.karan.do_it_now_motivation_tracker.screens.SettingsScreen(navController, goalViewModel)
                }

                // Category Analytics
                composable("analytics") {
                    CategoryAnalyticsScreen(goalViewModel)
                }

                // Character Screen
                composable("character") {
                    CharacterScreen(goalViewModel)
                }

                // Weekly Report
                composable("weeklyReport") {
                    WeeklyReportScreen(goalViewModel)
                }

                // Login Screen
                composable("login") {
                    LoginScreen(
                        viewModel = goalViewModel,
                        onLoginSuccess = { 
                            val dest = if (!prefs.hasOnboarded) "onboarding" else "dashboard"
                            navController.navigate(dest) {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onSkip = {
                            val dest = if (!prefs.hasOnboarded) "onboarding" else "dashboard"
                            navController.navigate(dest) {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // Leaderboard
                composable("leaderboard") {
                    val stats   = goalViewModel.userStats.collectAsState().value
                    val streak  = goalViewModel.currentStreak.collectAsState().value
                    val score   = (streak * 3) + (goalViewModel.completedGoalCount.collectAsState().value * 2)
                    LeaderboardScreen(
                        currentUid   = FirebaseManager.userId,
                        currentStreak = streak,
                        currentScore  = score,
                        currentLevel  = stats.level,
                        onRefresh     = { goalViewModel.forceUpdateLeaderboard() }
                    )
                }

                // AI Chat
                composable("ai_chat") {
                    AiChatScreen(navController, goalViewModel, aiViewModel)
                }
            }
        }
    }
}

// ── Pixel Bottom Nav ──────────────────────────────────────────────

@Composable
private fun PixelBottomNav(
    currentRoute: String?,
    onNavClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 2dp white top border
        Box(Modifier.fillMaxWidth().height(2.dp).background(Color.White))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // HOME
            PixelNavItem(
                icon     = { mod, col -> PixelIconHome(mod, col) },
                label    = "HOME",
                selected = currentRoute == "dashboard",
                onClick  = { onNavClick("dashboard") },
                modifier = Modifier.weight(1f)
            )

            // GOALS
            PixelNavItem(
                icon     = { mod, col -> PixelIconClipboard(mod, col) },
                label    = "GOALS",
                selected = currentRoute == "goals",
                onClick  = { onNavClick("goals") },
                modifier = Modifier.weight(1f)
            )

            // Center ADD button — raised white square
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .border(2.dp, Color.White)
                        .background(Color.White)
                        .clickable(onClick = onAddClick),
                    contentAlignment = Alignment.Center
                ) {
                    PixelIconPlus(
                        modifier = Modifier.size(26.dp),
                        color    = Color.Black
                    )
                }
            }

            // STATS
            PixelNavItem(
                icon     = { mod, col -> PixelIconBarChartNav(mod, col) },
                label    = "STATS",
                selected = currentRoute == "profile",
                onClick  = { onNavClick("profile") },
                modifier = Modifier.weight(1f)
            )

            // ANALYTICS
            PixelNavItem(
                icon     = { mod, col -> PixelIconBarChartNav(mod, col) },
                label    = "ANALYSIS",
                selected = currentRoute == "analytics",
                onClick  = { onNavClick("analytics") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PixelNavItem(
    icon: @Composable (Modifier, Color) -> Unit,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = if (selected) Color.White else Color(0xFF555555)
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Selected top indicator
        if (selected) {
            Box(Modifier.width(22.dp).height(2.dp).background(Color.White))
            Spacer(Modifier.height(4.dp))
        } else {
            Spacer(Modifier.height(6.dp))
        }
        icon(Modifier.size(20.dp), iconColor)
        Spacer(Modifier.height(3.dp))
        Text(
            label,
            color      = iconColor,
            fontSize   = 6.sp,
            fontFamily = PixelFontFamily,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.3.sp
        )
    }
}