package com.karan.do_it_now_motivation_tracker.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.karan.do_it_now_motivation_tracker.data.AppDatabase
import com.karan.do_it_now_motivation_tracker.util.NotificationHelper

class GoalTimerWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val goalId = inputData.getInt("goal_id", -1)
        if (goalId == -1) return Result.failure()

        val db = AppDatabase.getDatabase(applicationContext)
        val goal = db.goalDao().getGoalById(goalId) ?: return Result.failure()

        // Only show notification if the goal hasn't been completed or deleted
        if (!goal.isCompleted) {
            NotificationHelper.showGoalTimerStarted(applicationContext, goal.title, goal.id, goal.endDate)
        }

        return Result.success()
    }
}
