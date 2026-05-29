package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey
    val date: String,
    val goalsCompletedCount: Int = 0,
    val goalsFailedCount: Int = 0
)
