package com.karan.do_it_now_motivation_tracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_chat_messages")
data class AiChatMessage(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
