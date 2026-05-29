package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_chat_sessions")
data class AiChatSession(
    @PrimaryKey
    val id: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)
