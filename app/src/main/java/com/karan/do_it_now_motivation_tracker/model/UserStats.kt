package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey
    val id: Int = 1,          // singleton row
    val totalXp: Int = 0,
    val level: Int = 1,
    val levelTitle: String = "Rookie"
)

// ── Level thresholds ───────────────────────────────────────────
fun xpToLevelInfo(xp: Int): Triple<Int, String, Pair<Int, Int>> {
    val (level, title, threshold) = when {
        xp < 100  -> Triple(1, "Rookie",      100)
        xp < 300  -> Triple(2, "Apprentice",  300)
        xp < 600  -> Triple(3, "Challenger",  600)
        xp < 1000 -> Triple(4, "Expert",      1000)
        xp < 2000 -> Triple(5, "Master",      2000)
        else      -> Triple(6, "Legend",      Int.MAX_VALUE)
    }
    val prevThreshold = when (level) {
        1 -> 0; 2 -> 100; 3 -> 300; 4 -> 600; 5 -> 1000; else -> 2000
    }
    return Triple(level, title, (xp - prevThreshold) to (threshold - prevThreshold))
}
