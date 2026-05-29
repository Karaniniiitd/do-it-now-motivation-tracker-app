package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// ── Entity ────────────────────────────────────────────────────────

@Entity(tableName = "weekly_reports")
data class WeeklyReport(
    @PrimaryKey
    val weekStart: String,      // "yyyy-MM-dd" Monday of the week
    val goalsCompleted: Int = 0,
    val activeDays: Int = 0,    // days with at least 1 completion
    val xpEarned: Int = 0,
    val longestStreakInWeek: Int = 0,
    val grade: String = "F",    // A / B / C / D / F
    val generatedAt: Long = System.currentTimeMillis()
)

// ── Grade computation ─────────────────────────────────────────────

fun computeGrade(activeDays: Int, goalsCompleted: Int): String {
    val score = (activeDays * 10) + (goalsCompleted * 5)
    return when {
        score >= 80 -> "A"
        score >= 60 -> "B"
        score >= 40 -> "C"
        score >= 20 -> "D"
        else        -> "F"
    }
}

// ── DAO ───────────────────────────────────────────────────────────

@Dao
interface WeeklyReportDao {

    @Query("SELECT * FROM weekly_reports ORDER BY weekStart DESC")
    fun getAllReports(): Flow<List<WeeklyReport>>

    @Query("SELECT * FROM weekly_reports ORDER BY weekStart DESC LIMIT 1")
    suspend fun getLatestReport(): WeeklyReport?

    @Query("SELECT * FROM weekly_reports WHERE weekStart = :weekStart")
    suspend fun getReportForWeek(weekStart: String): WeeklyReport?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(report: WeeklyReport)

    @Query("DELETE FROM weekly_reports")
    suspend fun deleteAll()
}
