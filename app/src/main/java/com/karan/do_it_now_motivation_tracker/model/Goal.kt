package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val difficulty: String,           // Easy | Medium | Hard | Boss
    val category: String = "Mission",
    val isCompleted: Boolean = false,
    val completedDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val progressPercent: Int = 0,
    val notes: String = "",
    val recurrenceType: String? = null, // null | "daily" | "weekly" | "monthly"
    val hasTime: Boolean = false,
    val isFailed: Boolean = false,
    val failedDate: Long? = null
)

fun Goal.xpReward(): Int = when (difficulty) {
    "Easy"   -> 10
    "Medium" -> 25
    "Hard"   -> 50
    "Boss"   -> 100
    else     -> 10
}

fun Goal.recurrenceLabel(): String = when (recurrenceType) {
    "daily"   -> "DAILY"
    "weekly"  -> "WEEKLY"
    "monthly" -> "MONTHLY"
    else      -> ""
}