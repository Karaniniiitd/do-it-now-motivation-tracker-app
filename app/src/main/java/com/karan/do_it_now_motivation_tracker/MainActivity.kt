package com.karan.do_it_now_motivation_tracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.karan.do_it_now_motivation_tracker.navigation.AppNavigation
import com.karan.do_it_now_motivation_tracker.ui.theme.Do_It_Now_Motivation_TrackerTheme
import com.karan.do_it_now_motivation_tracker.util.NotificationHelper
import com.karan.do_it_now_motivation_tracker.workers.DailyReminderWorker
import com.karan.do_it_now_motivation_tracker.workers.StreakGuardWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* ignore result */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars    = false
            isAppearanceLightNavigationBars = false
        }

        // Create notification channels (safe to call every launch)
        NotificationHelper.createChannels(this)

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Schedule daily reminder at 9:00 AM (repeating every 24 hours)
        scheduleWorker<DailyReminderWorker>(
            tag          = "daily_reminder",
            intervalHours = 24L
        )

        // Schedule streak guard at 11:00 PM (repeating every 24 hours)
        scheduleWorker<StreakGuardWorker>(
            tag          = "streak_guard",
            intervalHours = 24L
        )

        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val prefs = com.karan.do_it_now_motivation_tracker.util.UserPrefsManager.getInstance(context)
            val themeMode by prefs.themeModeFlow.collectAsState(initial = prefs.themeMode)
            
            val isDark = when (themeMode) {
                com.karan.do_it_now_motivation_tracker.util.UserPrefsManager.ThemeMode.LIGHT -> false
                com.karan.do_it_now_motivation_tracker.util.UserPrefsManager.ThemeMode.DARK -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            Do_It_Now_Motivation_TrackerTheme(darkTheme = isDark) {
                AppNavigation()
            }
        }
    }

    private inline fun <reified W : androidx.work.ListenableWorker> scheduleWorker(
        tag: String,
        intervalHours: Long
    ) {
        val request = PeriodicWorkRequestBuilder<W>(intervalHours, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            tag,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}