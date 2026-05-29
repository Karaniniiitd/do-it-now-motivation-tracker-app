package com.karan.do_it_now_motivation_tracker.util

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.karan.do_it_now_motivation_tracker.model.DailyLog
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.model.UserStats
import com.karan.do_it_now_motivation_tracker.model.WeeklyReport
import kotlinx.coroutines.tasks.await

/**
 * All Firebase Realtime Database read/write operations.
 * Structure: users/{uid}/goals, stats, daily_logs, weekly_reports
 *            leaderboard/{uid}
 */
object RealtimeDatabaseSync {

    private val db: FirebaseDatabase = Firebase.database

    // ── Goals ─────────────────────────────────────────────────────

    fun uploadGoal(uid: String, goal: Goal) {
        db.getReference("users/$uid/goals/${goal.id}").setValue(goal.toMap())
    }

    fun deleteGoal(uid: String, goalId: Int) {
        db.getReference("users/$uid/goals/$goalId").removeValue()
    }

    suspend fun downloadGoals(uid: String): List<Goal> {
        return try {
            val snap = db.getReference("users/$uid/goals").get().await()
            snap.children.mapNotNull { child ->
                try {
                    val m = child.value as? Map<*, *> ?: return@mapNotNull null
                    Goal(
                        id              = (m["id"] as? Long)?.toInt() ?: 0,
                        title           = m["title"] as? String ?: "",
                        startDate       = (m["startDate"] as? Long) ?: 0L,
                        endDate         = (m["endDate"] as? Long) ?: 0L,
                        difficulty      = m["difficulty"] as? String ?: "Easy",
                        category        = m["category"] as? String ?: "Mission",
                        isCompleted     = m["isCompleted"] as? Boolean ?: false,
                        completedDate   = m["completedDate"] as? Long,
                        createdAt       = (m["createdAt"] as? Long) ?: System.currentTimeMillis(),
                        progressPercent = (m["progressPercent"] as? Long)?.toInt() ?: 0,
                        notes           = m["notes"] as? String ?: "",
                        recurrenceType  = m["recurrenceType"] as? String
                    )
                } catch (_: Exception) { null }
            }
        } catch (_: Exception) { emptyList() }
    }

    // ── Stats ─────────────────────────────────────────────────────

    fun uploadStats(uid: String, stats: UserStats) {
        db.getReference("users/$uid/stats/main").setValue(
            mapOf(
                "totalXp"      to stats.totalXp,
                "level"        to stats.level,
                "levelTitle"   to stats.levelTitle,
                "freezeTokens" to stats.freezeTokens
            )
        )
    }

    suspend fun downloadStats(uid: String): UserStats? {
        return try {
            val snap = db.getReference("users/$uid/stats/main").get().await()
            val m = snap.value as? Map<*, *> ?: return null
            UserStats(
                totalXp      = (m["totalXp"]      as? Long)?.toInt() ?: 0,
                level        = (m["level"]        as? Long)?.toInt() ?: 1,
                levelTitle   = m["levelTitle"]   as? String ?: "Rookie",
                freezeTokens = (m["freezeTokens"] as? Long)?.toInt() ?: 0
            )
        } catch (_: Exception) { null }
    }

    // ── Daily Logs ────────────────────────────────────────────────

    fun uploadDailyLog(uid: String, log: DailyLog) {
        db.getReference("users/$uid/daily_logs/${log.date}").setValue(
            mapOf("date" to log.date, "goalsCompletedCount" to log.goalsCompletedCount)
        )
    }

    suspend fun downloadDailyLogs(uid: String): List<DailyLog> {
        return try {
            val snap = db.getReference("users/$uid/daily_logs").get().await()
            snap.children.mapNotNull { child ->
                val m = child.value as? Map<*, *> ?: return@mapNotNull null
                DailyLog(
                    date                = m["date"] as? String ?: return@mapNotNull null,
                    goalsCompletedCount = (m["goalsCompletedCount"] as? Long)?.toInt() ?: 0
                )
            }
        } catch (_: Exception) { emptyList() }
    }

    // ── Weekly Reports ────────────────────────────────────────────

    fun uploadWeeklyReport(uid: String, report: WeeklyReport) {
        db.getReference("users/$uid/weekly_reports/${report.weekStart}").setValue(
            mapOf(
                "weekStart"          to report.weekStart,
                "goalsCompleted"     to report.goalsCompleted,
                "activeDays"         to report.activeDays,
                "xpEarned"           to report.xpEarned,
                "longestStreakInWeek" to report.longestStreakInWeek,
                "grade"              to report.grade
            )
        )
    }

    // ── Leaderboard ───────────────────────────────────────────────

    fun uploadLeaderboardEntry(
        uid: String,
        displayName: String,
        disciplineScore: Int,
        level: Int,
        streak: Int
    ) {
        db.getReference("leaderboard/$uid").setValue(
            mapOf(
                "uid"            to uid,
                "displayName"    to displayName,
                "disciplineScore" to disciplineScore,
                "level"          to level,
                "streak"         to streak,
                "updatedAt"      to System.currentTimeMillis()
            )
        )
    }

    data class LeaderboardEntry(
        val uid: String,
        val displayName: String,
        val disciplineScore: Int,
        val level: Int,
        val streak: Int
    )

    suspend fun downloadLeaderboard(): List<LeaderboardEntry> {
        return try {
            val snap = db.getReference("leaderboard")
                .orderByChild("disciplineScore")
                .limitToLast(50)
                .get().await()
            snap.children.mapNotNull { child ->
                val m = child.value as? Map<*, *> ?: return@mapNotNull null
                LeaderboardEntry(
                    uid             = m["uid"] as? String ?: "",
                    displayName     = m["displayName"] as? String ?: "UNKNOWN",
                    disciplineScore = (m["disciplineScore"] as? Long)?.toInt() ?: 0,
                    level           = (m["level"] as? Long)?.toInt() ?: 1,
                    streak          = (m["streak"] as? Long)?.toInt() ?: 0
                )
            }.sortedByDescending { it.disciplineScore }
        } catch (_: Exception) { emptyList() }
    }

    // ── Full restore ──────────────────────────────────────────────

    /** Returns true if cloud has any data for this user */
    suspend fun hasCloudData(uid: String): Boolean {
        return try {
            val snap = db.getReference("users/$uid").get().await()
            snap.exists()
        } catch (_: Exception) { false }
    }
}

// ── Goal extension helper ─────────────────────────────────────────

private fun Goal.toMap(): Map<String, Any?> = mapOf(
    "id"              to id,
    "title"           to title,
    "startDate"       to startDate,
    "endDate"         to endDate,
    "difficulty"      to difficulty,
    "category"        to category,
    "isCompleted"     to isCompleted,
    "completedDate"   to completedDate,
    "createdAt"       to createdAt,
    "progressPercent" to progressPercent,
    "notes"           to notes,
    "recurrenceType"  to recurrenceType
)
