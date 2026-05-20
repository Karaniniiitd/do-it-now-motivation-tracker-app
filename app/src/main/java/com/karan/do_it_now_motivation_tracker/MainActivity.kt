package com.karan.do_it_now_motivation_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.karan.do_it_now_motivation_tracker.navigation.AppNavigation
import com.karan.do_it_now_motivation_tracker.ui.theme.Do_It_Now_Motivation_TrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Force dark status bar icons for dark theme
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            Do_It_Now_Motivation_TrackerTheme {
                AppNavigation()
            }
        }
    }
}