package com.karan.do_it_now_motivation_tracker.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.karan.do_it_now_motivation_tracker.MainActivity
import com.karan.do_it_now_motivation_tracker.R

object NotificationHelper {

    const val CHANNEL_DAILY    = "do_it_now_daily"
    const val CHANNEL_STREAK   = "do_it_now_streak"
    const val CHANNEL_DEADLINE = "do_it_now_deadline"
    const val CHANNEL_HOURLY   = "do_it_now_hourly"

    const val NOTIF_DAILY_ID    = 1001
    const val NOTIF_STREAK_ID   = 1002
    const val NOTIF_DEADLINE_ID = 1003
    const val NOTIF_HOURLY_ID   = 1004

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return

        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_DAILY, "Daily Check-In", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Daily reminder to complete your missions"
            }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_STREAK, "Streak Guard", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Alerts when your streak is in danger"
            }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_DEADLINE, "Deadline Alert", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Alerts when a mission deadline is near"
            }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_HOURLY, "Hourly Reminder", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Contextual hourly reminders for active missions"
            }
        )
    }

    private fun launchIntent(context: Context, goalId: Int? = null): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (goalId != null) {
                putExtra("focus_goal_id", goalId)
            }
        }
        val requestCode = goalId ?: 0
        return PendingIntent.getActivity(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun showDailyReminder(context: Context) {
        val messages = listOf(
            "DAILY MISSION ACTIVE. REPORT IN.",
            "YOUR STREAK IS WAITING. DON'T BREAK IT.",
            "COMPLETE A MISSION. BUILD THE HABIT.",
            "WARRIORS DON'T REST. OPEN YOUR MISSIONS.",
            "DISCIPLINE > MOTIVATION. CHECK IN NOW."
        )
        val msg = messages[System.currentTimeMillis().toInt().and(0x7FFFFFFF) % messages.size]

        val notif = NotificationCompat.Builder(context, CHANNEL_DAILY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("DO IT NOW")
            .setContentText(msg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(launchIntent(context))
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            ?.notify(NOTIF_DAILY_ID, notif)
    }

    fun showStreakWarning(context: Context, streakDays: Int) {
        val text = "YOUR $streakDays-DAY STREAK ENDS AT MIDNIGHT. COMPLETE A MISSION NOW."
        val notif = NotificationCompat.Builder(context, CHANNEL_STREAK)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("STREAK IN DANGER")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(launchIntent(context))
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            ?.notify(NOTIF_STREAK_ID, notif)
    }

    fun showDeadlineAlert(context: Context, goalTitle: String, hoursLeft: Int) {
        val text = "MISSION \"${goalTitle.uppercase()}\" EXPIRES IN $hoursLeft HOURS."
        val notif = NotificationCompat.Builder(context, CHANNEL_DEADLINE)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("MISSION EXPIRING SOON")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(launchIntent(context))
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            ?.notify(NOTIF_DEADLINE_ID, notif)
    }

    fun showHourlyContextualReminder(context: Context, goalTitle: String) {
        val messages = listOf(
            "DON'T FORGET YOUR MISSION: $goalTitle",
            "THE WARRIOR GRINDS: $goalTitle IS WAITING.",
            "STAY FOCUSED. PROGRESS ON: $goalTitle",
            "TIME TO CONQUER: $goalTitle"
        )
        val msg = messages[System.currentTimeMillis().toInt().and(0x7FFFFFFF) % messages.size]

        val notif = NotificationCompat.Builder(context, CHANNEL_HOURLY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("MISSION REMINDER")
            .setContentText(msg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            .setPriority(NotificationCompat.PRIORITY_LOW) // Low priority so it doesn't disturb too much
            .setContentIntent(launchIntent(context))
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            ?.notify(NOTIF_HOURLY_ID, notif)
    }

    fun showGoalTimerStarted(context: Context, goalTitle: String, goalId: Int, endTimeMillis: Long) {
        val text = "YOUR TIMED MISSION \"${goalTitle.uppercase()}\" HAS BEGUN."
        val builder = NotificationCompat.Builder(context, CHANNEL_HOURLY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("MISSION TIMER")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(launchIntent(context))
            .setAutoCancel(true)
            .setUsesChronometer(true)
            .setWhen(endTimeMillis)
            .setOngoing(true) // Keep it ongoing while active

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setChronometerCountDown(true)
        }
        val timeRemaining = endTimeMillis - System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && timeRemaining > 0) {
            builder.setTimeoutAfter(timeRemaining)
        }

        val notif = builder.build()

        // Use a unique notification ID based on the goal ID
        context.getSystemService(NotificationManager::class.java)
            ?.notify(NOTIF_HOURLY_ID + goalId, notif)
    }

    fun cancelGoalTimerNotification(context: Context, goalId: Int) {
        context.getSystemService(NotificationManager::class.java)
            ?.cancel(NOTIF_HOURLY_ID + goalId)
    }

    fun showGoalTimerEnded(context: Context, goalTitle: String, goalId: Int) {
        val text = "TIME IS UP FOR \"${goalTitle.uppercase()}\". DID YOU COMPLETE IT?"
        val builder = NotificationCompat.Builder(context, CHANNEL_DEADLINE) // Use deadline channel for high priority/sound
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("MISSION TIMER ENDED")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(launchIntent(context, goalId))
            .setAutoCancel(true)

        val notif = builder.build()

        context.getSystemService(NotificationManager::class.java)
            ?.notify(NOTIF_HOURLY_ID + goalId, notif)
    }
}
