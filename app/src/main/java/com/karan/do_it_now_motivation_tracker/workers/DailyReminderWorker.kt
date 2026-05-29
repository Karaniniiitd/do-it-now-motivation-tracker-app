package com.karan.do_it_now_motivation_tracker.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.karan.do_it_now_motivation_tracker.data.AppDatabase
import com.karan.do_it_now_motivation_tracker.util.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fires once per day (9:00 AM by default) — shows the daily check-in notification.
 * Also checks if any active goal has a deadline within 24 hours.
 */
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db  = AppDatabase.getDatabase(applicationContext)
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Daily check-in push
        NotificationHelper.showDailyReminder(applicationContext)

        // Deadline alert — any active goal due within 24 hours
        val now       = System.currentTimeMillis()
        val in24Hours = now + 24 * 60 * 60 * 1000L
        val goals     = db.goalDao().getActiveGoalsOnce()
        goals.filter { it.endDate in now..in24Hours }.forEach { goal ->
            val hoursLeft = ((goal.endDate - now) / (1000 * 60 * 60)).toInt().coerceAtLeast(1)
            NotificationHelper.showDeadlineAlert(applicationContext, goal.title, hoursLeft)
        }

        return Result.success()
    }
}
