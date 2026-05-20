package com.karan.do_it_now_motivation_tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.karan.do_it_now_motivation_tracker.data.AppDatabase
import com.karan.do_it_now_motivation_tracker.model.DailyLog
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.model.UserStats
import com.karan.do_it_now_motivation_tracker.model.xpReward
import com.karan.do_it_now_motivation_tracker.model.xpToLevelInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GoalViewModel(application: Application) : AndroidViewModel(application) {

    private val db          = AppDatabase.getDatabase(application)
    private val goalDao     = db.goalDao()
    private val dailyLogDao = db.dailyLogDao()
    private val statsDao    = db.userStatsDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // ── Goal streams ───────────────────────────────────────────
    val activeGoals: StateFlow<List<Goal>> = goalDao.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedGoals: StateFlow<List<Goal>> = goalDao.getCompletedGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGoals: StateFlow<Int> = goalDao.getTotalGoalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedGoalCount: StateFlow<Int> = goalDao.getCompletedGoalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ── Today's Focus (earliest deadline active goal) ──────────
    val todaysFocus: StateFlow<Goal?> = activeGoals
        .map { list -> list.minByOrNull { it.endDate } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── Daily logs for streak + heatmap ───────────────────────
    private fun yearAgoDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -365)
        return dateFormat.format(cal.time)
    }

    val yearlyLogs: StateFlow<List<DailyLog>> = dailyLogDao.getLogsFrom(yearAgoDate())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allLogs: StateFlow<List<DailyLog>> = dailyLogDao.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStreak: StateFlow<Int> = allLogs
        .map { calculateStreak(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val longestStreak: StateFlow<Int> = allLogs
        .map { calculateLongestStreak(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ── User XP / Level ────────────────────────────────────────
    val userStats: StateFlow<UserStats> = statsDao.getUserStats()
        .map { it ?: UserStats() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStats())

    // ── Goal CRUD ──────────────────────────────────────────────
    fun addGoal(goal: Goal) = viewModelScope.launch { goalDao.insert(goal) }

    fun updateGoal(goal: Goal) = viewModelScope.launch { goalDao.update(goal) }

    fun deleteGoal(goal: Goal) = viewModelScope.launch { goalDao.delete(goal) }

    fun completeGoal(goal: Goal) = viewModelScope.launch {
        val now = System.currentTimeMillis()
        goalDao.update(goal.copy(isCompleted = true, completedDate = now))

        // Record daily log
        val today = dateFormat.format(Date(now))
        val existing = dailyLogDao.getLogForDate(today)
        dailyLogDao.insertOrUpdate(
            if (existing != null)
                existing.copy(goalsCompletedCount = existing.goalsCompletedCount + 1)
            else
                DailyLog(date = today, goalsCompletedCount = 1)
        )

        // Award XP (read current from StateFlow cache)
        val newXp = userStats.value.totalXp + goal.xpReward()
        val (level, title, _) = xpToLevelInfo(newXp)
        statsDao.upsert(UserStats(totalXp = newXp, level = level, levelTitle = title))
    }

    fun uncompleteGoal(goal: Goal) = viewModelScope.launch {
        goalDao.update(goal.copy(isCompleted = false, completedDate = null))
    }

    suspend fun getGoalById(id: Int): Goal? = goalDao.getGoalById(id)

    fun clearAllData() = viewModelScope.launch {
        goalDao.deleteAll()
        dailyLogDao.deleteAll()
        statsDao.upsert(UserStats())
    }

    // ── Streak calculation ─────────────────────────────────────
    private fun calculateStreak(logs: List<DailyLog>): Int {
        if (logs.isEmpty()) return 0
        val cal = Calendar.getInstance()
        var streak = 0
        while (true) {
            val dateStr = dateFormat.format(cal.time)
            val log = logs.find { it.date == dateStr }
            if (log != null && log.goalsCompletedCount > 0) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else break
        }
        return streak
    }

    private fun calculateLongestStreak(logs: List<DailyLog>): Int {
        val sorted = logs.filter { it.goalsCompletedCount > 0 }.map { it.date }.sorted()
        if (sorted.isEmpty()) return 0
        var longest = 1; var current = 1
        for (i in 1 until sorted.size) {
            val prev = Calendar.getInstance().apply { time = dateFormat.parse(sorted[i - 1]) ?: Date() }
            val curr = Calendar.getInstance().apply { time = dateFormat.parse(sorted[i]) ?: Date() }
            prev.add(Calendar.DAY_OF_YEAR, 1)
            if (prev.get(Calendar.YEAR) == curr.get(Calendar.YEAR) &&
                prev.get(Calendar.DAY_OF_YEAR) == curr.get(Calendar.DAY_OF_YEAR)) {
                current++; if (current > longest) longest = current
            } else current = 1
        }
        return longest
    }
}