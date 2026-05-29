package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Types ─────────────────────────────────────────────────────────

enum class QuestType {
    COMPLETE_N_GOALS,          // Complete N goals today
    COMPLETE_HARD_GOAL,        // Complete 1 Hard or Boss goal
    LOG_STREAK,                // Maintain / extend your streak
    COMPLETE_CATEGORY_GOAL,    // Complete a goal in a specific category
    ADD_PROGRESS               // Update progress on any goal
}

// ── Entity ────────────────────────────────────────────────────────

@Entity(tableName = "daily_quests")
data class DailyQuest(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,               // "yyyy-MM-dd"
    val type: String,               // QuestType.name
    val description: String,        // Human-readable label shown in UI
    val targetValue: Int = 1,       // How many times to complete
    val currentValue: Int = 0,      // Current progress toward target
    val xpReward: Int = 30,         // XP awarded on completion
    val isCompleted: Boolean = false
)

val DailyQuest.progress: Float get() = (currentValue.toFloat() / targetValue).coerceIn(0f, 1f)

// ── DAO ───────────────────────────────────────────────────────────

@Dao
interface DailyQuestDao {

    @Query("SELECT * FROM daily_quests WHERE date = :date ORDER BY id ASC")
    fun getQuestsForDate(date: String): Flow<List<DailyQuest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quests: List<DailyQuest>)

    @Query("UPDATE daily_quests SET currentValue = :value, isCompleted = :done WHERE id = :id")
    suspend fun updateProgress(id: Int, value: Int, done: Boolean)

    @Query("SELECT COUNT(*) FROM daily_quests WHERE date = :date")
    suspend fun countForDate(date: String): Int

    @Query("DELETE FROM daily_quests WHERE date < :cutoff")
    suspend fun deleteOlderThan(cutoff: String)
}

// ── Quest Generator ───────────────────────────────────────────────

object QuestGenerator {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val categories = listOf("Mind", "Body", "Code", "Work", "Finance", "Create", "Spirit", "Mission")

    fun generateForDate(date: String = dateFormat.format(Date())): List<DailyQuest> {
        // Seed random from date string so same day always gets same quests
        val seed = date.replace("-", "").toLongOrNull() ?: System.currentTimeMillis()
        val rng = java.util.Random(seed)

        val pool = listOf(
            DailyQuest(
                date        = date,
                type        = QuestType.COMPLETE_N_GOALS.name,
                description = "COMPLETE 1 GOAL TODAY",
                targetValue = 1,
                xpReward    = 20
            ),
            DailyQuest(
                date        = date,
                type        = QuestType.COMPLETE_N_GOALS.name,
                description = "COMPLETE 2 GOALS TODAY",
                targetValue = 2,
                xpReward    = 40
            ),
            DailyQuest(
                date        = date,
                type        = QuestType.COMPLETE_HARD_GOAL.name,
                description = "DEFEAT A HARD OR BOSS MISSION",
                targetValue = 1,
                xpReward    = 60
            ),
            DailyQuest(
                date        = date,
                type        = QuestType.LOG_STREAK.name,
                description = "KEEP YOUR STREAK ALIVE",
                targetValue = 1,
                xpReward    = 30
            ),
            DailyQuest(
                date        = date,
                type        = QuestType.COMPLETE_CATEGORY_GOAL.name,
                description = "COMPLETE A ${categories[rng.nextInt(categories.size)].uppercase()} MISSION",
                targetValue = 1,
                xpReward    = 25
            ),
            DailyQuest(
                date        = date,
                type        = QuestType.ADD_PROGRESS.name,
                description = "UPDATE PROGRESS ON ANY MISSION",
                targetValue = 1,
                xpReward    = 15
            )
        )

        // Always pick exactly 3 different quests
        return pool.shuffled(rng).take(3)
    }
}
