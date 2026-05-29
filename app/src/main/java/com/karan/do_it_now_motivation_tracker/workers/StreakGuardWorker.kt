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
 * Fires at 11:00 PM — if the user hasn't completed any goal today, warns that
 * their streak is about to break. Only fires if streak > 0.
 */
class StreakGuardWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db  = AppDatabase.getDatabase(applicationContext)
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val today = fmt.format(Date())
        val todayLog = db.dailyLogDao().getLogForDate(today)

        // If they already logged completions today, no warning needed
        if (todayLog != null && todayLog.goalsCompletedCount > 0) return Result.success()

        // Calculate current streak
        val allLogs = db.dailyLogDao().getAllLogsOnce()
        val streak  = calculateStreak(allLogs.map { it.date }, fmt)

        if (streak > 0) {
            NotificationHelper.showStreakWarning(applicationContext, streak)
        }

        return Result.success()
    }

    private fun calculateStreak(dates: List<String>, fmt: SimpleDateFormat): Int {
        val cal = java.util.Calendar.getInstance()
        var streak = 0
        // Check from yesterday backward (today hasn't been logged yet)
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        while (true) {
            val dateStr = fmt.format(cal.time)
            if (dates.contains(dateStr)) {
                streak++
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else break
        }
        return streak
    }
}
