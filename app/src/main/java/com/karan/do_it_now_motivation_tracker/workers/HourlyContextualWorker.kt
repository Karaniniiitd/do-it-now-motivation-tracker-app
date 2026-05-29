package com.karan.do_it_now_motivation_tracker.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.karan.do_it_now_motivation_tracker.data.AppDatabase
import com.karan.do_it_now_motivation_tracker.util.NotificationHelper
import java.util.Calendar

class HourlyContextualWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val activeGoals = db.goalDao().getActiveGoalsOnce()

        // Only send hourly reminders during waking hours (e.g., 8 AM to 10 PM)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hour < 8 || hour >= 22) {
            return Result.success()
        }

        if (activeGoals.isNotEmpty()) {
            val randomGoal = activeGoals.random()
            NotificationHelper.showHourlyContextualReminder(applicationContext, randomGoal.title)
        }

        return Result.success()
    }
}
