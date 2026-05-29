package com.karan.do_it_now_motivation_tracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.karan.do_it_now_motivation_tracker.data.AppDatabase
import com.karan.do_it_now_motivation_tracker.model.DailyLog
import com.karan.do_it_now_motivation_tracker.model.DailyQuest
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.model.QuestGenerator
import com.karan.do_it_now_motivation_tracker.model.QuestType
import com.karan.do_it_now_motivation_tracker.model.UserStats
import com.karan.do_it_now_motivation_tracker.model.WeeklyReport
import com.karan.do_it_now_motivation_tracker.model.computeGrade
import com.karan.do_it_now_motivation_tracker.model.xpReward
import com.karan.do_it_now_motivation_tracker.model.xpToLevelInfo
import com.karan.do_it_now_motivation_tracker.util.FirebaseManager
import com.karan.do_it_now_motivation_tracker.util.RealtimeDatabaseSync
import com.karan.do_it_now_motivation_tracker.util.SoundManager
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

    private val db              = AppDatabase.getDatabase(application)
    private val goalDao         = db.goalDao()
    private val dailyLogDao     = db.dailyLogDao()
    private val statsDao        = db.userStatsDao()
    private val questDao        = db.dailyQuestDao()
    private val weeklyReportDao = db.weeklyReportDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today get() = dateFormat.format(Date())

    // ── Goal streams ───────────────────────────────────────────
    val activeGoals: StateFlow<List<Goal>> = goalDao.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedGoals: StateFlow<List<Goal>> = goalDao.getCompletedGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGoals: StateFlow<Int> = goalDao.getTotalGoalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedGoalCount: StateFlow<Int> = goalDao.getCompletedGoalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todaysFocus: StateFlow<Goal?> = activeGoals
        .map { list -> list.minByOrNull { it.endDate } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── Daily logs ────────────────────────────────────────────
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

    // ── User stats ────────────────────────────────────────────
    val userStats: StateFlow<UserStats> = statsDao.getUserStats()
        .map { it ?: UserStats() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStats())

    // ── Daily quests ──────────────────────────────────────────
    val todaysQuests: StateFlow<List<DailyQuest>> = questDao.getQuestsForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun ensureTodaysQuests() = viewModelScope.launch {
        if (questDao.countForDate(today) == 0) {
            questDao.insertAll(QuestGenerator.generateForDate(today))
        }
    }

    // ── Weekly reports ────────────────────────────────────────
    val allWeeklyReports: StateFlow<List<WeeklyReport>> = weeklyReportDao.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Generate last week's report if not already generated (call on app start) */
    fun ensureWeeklyReport() = viewModelScope.launch {
        val cal = Calendar.getInstance()
        // Go to last Monday
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val weekStart = dateFormat.format(cal.time)

        if (weeklyReportDao.getReportForWeek(weekStart) != null) return@launch

        // Collect logs for that week (Mon–Sun)
        val weekEnd = Calendar.getInstance().apply {
            time = cal.time; add(Calendar.DAY_OF_YEAR, 6)
        }
        val logs = dailyLogDao.getAllLogsOnce()
        val weekLogs = logs.filter {
            val d = dateFormat.parse(it.date) ?: return@filter false
            d >= cal.time && d <= weekEnd.time
        }

        val activeDays    = weekLogs.count { it.goalsCompletedCount > 0 }
        val goalsCompleted = weekLogs.sumOf { it.goalsCompletedCount }

        // XP earned: approximate from completed goals that week
        val weekStartMs = cal.timeInMillis
        val weekEndMs   = weekEnd.timeInMillis
        val completedThisWeek = goalDao.getActiveGoalsOnce() // already completed — approximate
        val xpEarned = goalsCompleted * 25 // avg XP estimate

        val grade = computeGrade(activeDays, goalsCompleted)

        weeklyReportDao.insertOrUpdate(
            WeeklyReport(
                weekStart             = weekStart,
                goalsCompleted        = goalsCompleted,
                activeDays            = activeDays,
                xpEarned              = xpEarned,
                longestStreakInWeek   = activeDays, // simplified
                grade                 = grade
            )
        )
    }

    // ── Quest ticking ─────────────────────────────────────────
    fun tickQuests(type: QuestType, increment: Int = 1) = viewModelScope.launch {
        val quests = todaysQuests.value.filter { !it.isCompleted && it.type == type.name }
        quests.forEach { quest ->
            val newVal = (quest.currentValue + increment).coerceAtMost(quest.targetValue)
            val done   = newVal >= quest.targetValue
            questDao.updateProgress(quest.id, newVal, done)
            if (done) {
                val stats = userStats.value
                val newXp = stats.totalXp + quest.xpReward
                val (level, title, _) = xpToLevelInfo(newXp)
                val oldLevel = stats.level
                statsDao.upsert(UserStats(
                    totalXp      = newXp,
                    level        = level,
                    levelTitle   = title,
                    freezeTokens = stats.freezeTokens
                ))
                SoundManager.playQuestComplete()
                if (level > oldLevel) SoundManager.playLevelUp()
            }
        }
    }

    // ── Goal CRUD ─────────────────────────────────────────────
    fun addGoal(goal: Goal) = viewModelScope.launch {
        goalDao.insert(goal)
        FirebaseManager.userId?.let { uid -> RealtimeDatabaseSync.uploadGoal(uid, goal) }
    }

    fun updateGoal(goal: Goal) = viewModelScope.launch {
        goalDao.update(goal)
        FirebaseManager.userId?.let { uid -> RealtimeDatabaseSync.uploadGoal(uid, goal) }
        if (goal.progressPercent > 0) tickQuests(QuestType.ADD_PROGRESS)
        if (goal.progressPercent >= 100 && !goal.isCompleted) {
            completeGoal(goal.copy(progressPercent = 100))
        }
    }

    fun updateProgressOnly(goalId: Int, progress: Int) = viewModelScope.launch {
        goalDao.updateProgress(goalId, progress)
        tickQuests(QuestType.ADD_PROGRESS)
    }

    fun deleteGoal(goal: Goal) = viewModelScope.launch {
        goalDao.delete(goal)
        FirebaseManager.userId?.let { uid -> RealtimeDatabaseSync.deleteGoal(uid, goal.id) }
    }

    fun completeGoal(goal: Goal) = viewModelScope.launch {
        val now    = System.currentTimeMillis()
        val stats  = userStats.value

        goalDao.update(goal.copy(isCompleted = true, completedDate = now, progressPercent = 100))

        // Record daily log
        val existing = dailyLogDao.getLogForDate(today)
        dailyLogDao.insertOrUpdate(
            if (existing != null)
                existing.copy(goalsCompletedCount = existing.goalsCompletedCount + 1)
            else
                DailyLog(date = today, goalsCompletedCount = 1)
        )

        // Award XP
        val oldLevel = stats.level
        val newXp    = stats.totalXp + goal.xpReward()
        val (level, title, _) = xpToLevelInfo(newXp)

        // Freeze token: Boss goal earns 1 token
        val newFreezeTokens = if (goal.difficulty == "Boss")
            stats.freezeTokens + 1 else stats.freezeTokens

        statsDao.upsert(UserStats(
            totalXp      = newXp,
            level        = level,
            levelTitle   = title,
            freezeTokens = newFreezeTokens
        ))

        // Sounds
        SoundManager.playComplete()
        if (level > oldLevel) SoundManager.playLevelUp()

        // Tick quests
        tickQuests(QuestType.COMPLETE_N_GOALS)
        tickQuests(QuestType.LOG_STREAK)
        if (goal.difficulty == "Hard" || goal.difficulty == "Boss") {
            tickQuests(QuestType.COMPLETE_HARD_GOAL)
        }
        tickQuests(QuestType.COMPLETE_CATEGORY_GOAL)

        // ── Sync completed goal to Firebase ─────────────────
        val completedGoal = goal.copy(isCompleted = true, completedDate = now, progressPercent = 100)
        val newStats = UserStats(totalXp = newXp, level = level, levelTitle = title, freezeTokens = newFreezeTokens)
        FirebaseManager.userId?.let { uid ->
            RealtimeDatabaseSync.uploadGoal(uid, completedGoal)
            RealtimeDatabaseSync.uploadStats(uid, newStats)
            val todayLog = dailyLogDao.getLogForDate(today)
            if (todayLog != null) RealtimeDatabaseSync.uploadDailyLog(uid, todayLog)
            // Update leaderboard
            uploadLeaderboard(uid, level, newStats.freezeTokens)
        }

        // ── Recurring goal: auto-recreate ───────────────────
        if (goal.recurrenceType != null) {
            val durationMs   = goal.endDate - goal.startDate
            val newStartDate = now
            val newEndDate   = when (goal.recurrenceType) {
                "daily"   -> now + 86_400_000L
                "weekly"  -> now + 7  * 86_400_000L
                "monthly" -> now + 30 * 86_400_000L
                else      -> now + durationMs
            }
            goalDao.insert(
                goal.copy(
                    id              = 0,
                    isCompleted     = false,
                    completedDate   = null,
                    progressPercent = 0,
                    startDate       = newStartDate,
                    endDate         = newEndDate,
                    createdAt       = now
                )
            )
        }
    }

    fun uncompleteGoal(goal: Goal) = viewModelScope.launch {
        val updated = goal.copy(isCompleted = false, completedDate = null, progressPercent = 0)
        goalDao.update(updated)
        FirebaseManager.userId?.let { uid -> RealtimeDatabaseSync.uploadGoal(uid, updated) }
    }

    /** Spend a freeze token to protect today's streak */
    fun spendFreezeToken() = viewModelScope.launch {
        val stats = userStats.value
        if (stats.freezeTokens <= 0) return@launch
        val existing = dailyLogDao.getLogForDate(today)
        val log = if (existing == null || existing.goalsCompletedCount == 0) {
            val newLog = DailyLog(date = today, goalsCompletedCount = 1)
            dailyLogDao.insertOrUpdate(newLog)
            newLog
        } else existing
        val newStats = stats.copy(freezeTokens = stats.freezeTokens - 1)
        statsDao.upsert(newStats)
        FirebaseManager.userId?.let { uid ->
            RealtimeDatabaseSync.uploadDailyLog(uid, log)
            RealtimeDatabaseSync.uploadStats(uid, newStats)
        }
    }

    suspend fun getGoalById(id: Int): Goal? = goalDao.getGoalById(id)

    fun clearAllData() = viewModelScope.launch {
        goalDao.deleteAll()
        dailyLogDao.deleteAll()
        statsDao.upsert(UserStats())
    }

    // ── Firebase: restore all data from cloud ─────────────────
    fun restoreFromCloud() = viewModelScope.launch {
        val uid = FirebaseManager.userId ?: return@launch
        if (!RealtimeDatabaseSync.hasCloudData(uid)) return@launch

        val cloudGoals = RealtimeDatabaseSync.downloadGoals(uid)
        val cloudStats = RealtimeDatabaseSync.downloadStats(uid)
        val cloudLogs  = RealtimeDatabaseSync.downloadDailyLogs(uid)

        cloudGoals.forEach { goalDao.insert(it) }
        cloudLogs.forEach  { dailyLogDao.insertOrUpdate(it) }
        cloudStats?.let    { statsDao.upsert(it) }
    }

    // ── Firebase: push leaderboard score ──────────────────────
    private fun uploadLeaderboard(uid: String, level: Int, freezeTokens: Int) {
        val score  = ((currentStreak.value * 3) + (completedGoalCount.value * 2)).coerceAtMost(100)
        RealtimeDatabaseSync.uploadLeaderboardEntry(
            uid            = uid,
            displayName    = FirebaseManager.displayName,
            disciplineScore = score,
            level          = level,
            streak         = currentStreak.value
        )
    }

    // ── Streak calculation (freeze-aware) ────────────────────
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