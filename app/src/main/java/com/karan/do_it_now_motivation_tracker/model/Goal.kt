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
    val difficulty: String,
    val category: String = "General",
    val isCompleted: Boolean = false,
    val completedDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun Goal.xpReward(): Int = when (difficulty) {
    "Easy"   -> 10
    "Medium" -> 25
    "Hard"   -> 50
    else     -> 10
}