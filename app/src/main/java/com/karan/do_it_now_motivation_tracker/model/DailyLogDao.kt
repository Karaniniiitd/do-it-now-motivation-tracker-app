package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE date >= :startDate ORDER BY date ASC")
    fun getLogsFrom(startDate: String): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE date = :date")
    suspend fun getLogForDate(date: String): DailyLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DailyLog)

    @Query("DELETE FROM daily_logs")
    suspend fun deleteAll()

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    suspend fun getAllLogsOnce(): List<DailyLog>
}
