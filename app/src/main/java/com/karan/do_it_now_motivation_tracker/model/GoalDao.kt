package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals WHERE isCompleted = 0 AND isFailed = 0 ORDER BY endDate ASC")
    fun getActiveGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY completedDate DESC")
    fun getCompletedGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Int): Goal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Query("DELETE FROM goals")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM goals")
    fun getTotalGoalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM goals WHERE isCompleted = 1")
    fun getCompletedGoalCount(): Flow<Int>

    @Query("SELECT * FROM goals WHERE isFailed = 1 ORDER BY failedDate DESC")
    fun getFailedGoals(): Flow<List<Goal>>

    @Query("SELECT COUNT(*) FROM goals WHERE isFailed = 1")
    fun getFailedGoalCount(): Flow<Int>

    // One-shot (suspend) — used by Workers that can't collect flows
    @Query("SELECT * FROM goals WHERE isCompleted = 0 AND isFailed = 0 ORDER BY endDate ASC")
    suspend fun getActiveGoalsOnce(): List<Goal>

    // Update only progress fields without overwriting everything
    @Query("UPDATE goals SET progressPercent = :progress WHERE id = :id")
    suspend fun updateProgress(id: Int, progress: Int)
}
