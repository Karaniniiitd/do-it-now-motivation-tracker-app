package com.karan.do_it_now_motivation_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.karan.do_it_now_motivation_tracker.BuildConfig
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.model.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.collect
import com.karan.do_it_now_motivation_tracker.model.AiChatDao
import com.karan.do_it_now_motivation_tracker.model.AiChatMessage
import com.karan.do_it_now_motivation_tracker.model.AiChatSession
import androidx.lifecycle.ViewModelProvider

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false,
    var hasTyped: Boolean = false // Track typing state
)

class AiChatViewModel(private val dao: AiChatDao) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _sessions = MutableStateFlow<List<AiChatSession>>(emptyList())
    val sessions: StateFlow<List<AiChatSession>> = _sessions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var generativeModel: GenerativeModel? = null
    private var chatSession: com.google.ai.client.generativeai.Chat? = null
    private var currentSessionId: String? = null
    
    private var systemPromptText: String = ""

    init {
        viewModelScope.launch {
            dao.getAllSessions().collect {
                _sessions.value = it
            }
        }
    }

    fun initialize(userName: String, stats: UserStats, activeGoals: List<Goal>) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            _messages.value = listOf(
                ChatMessage(
                    text = "API Key not found. Please add your GEMINI_API_KEY to local.properties and rebuild.",
                    isUser = false,
                    isError = true
                )
            )
            return
        }

        val goalsText = if (activeGoals.isEmpty()) "No active goals right now." else
            activeGoals.joinToString("\n") { "- ${it.title} (${it.difficulty})" }

        systemPromptText = """
            You are a strict but encouraging, pixel-art themed 'Warrior' AI assistant for the 'Do It Now' motivation tracker app.
            The user's name is $userName. 
            They are currently Level ${stats.level} (${stats.levelTitle}) with ${stats.totalXp} XP.
            Their current active goals are:
            $goalsText
            
            Keep your answers concise, motivational, and slightly gamified. Use short sentences.
        """.trimIndent()

        generativeModel = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = apiKey,
            systemInstruction = content { text(systemPromptText) }
        )

        // If no session is active and no messages, start a fresh one
        if (currentSessionId == null && _messages.value.isEmpty()) {
            startNewSession(userName)
        }
    }

    fun startNewSession(userName: String) {
        val sessionId = java.util.UUID.randomUUID().toString()
        currentSessionId = sessionId
        
        chatSession = generativeModel?.startChat()
        
        val greeting = ChatMessage(text = "Greetings, Warrior $userName. I am your AI assistant. How can I help you conquer your goals today?", isUser = false)
        _messages.value = listOf(greeting)
        
        viewModelScope.launch {
            dao.insertSession(AiChatSession(id = sessionId, title = "New Chat"))
            dao.insertMessage(AiChatMessage(
                id = greeting.id,
                sessionId = sessionId,
                text = greeting.text,
                isUser = greeting.isUser,
                isError = greeting.isError
            ))
        }
    }

    fun loadSession(sessionId: String) {
        currentSessionId = sessionId
        viewModelScope.launch {
            dao.getMessagesForSession(sessionId).collect { aiMessages ->
                val loadedMessages = aiMessages.map {
                    ChatMessage(
                        id = it.id,
                        text = it.text,
                        isUser = it.isUser,
                        isError = it.isError,
                        hasTyped = true // Past messages are already typed
                    )
                }
                _messages.value = loadedMessages
                
                // Reconstruct Google AI chat session history
                val historyContents = loadedMessages.filter { !it.isError }.map { msg ->
                    content(if (msg.isUser) "user" else "model") { text(msg.text) }
                }
                chatSession = generativeModel?.startChat(historyContents)
            }
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            dao.deleteSession(sessionId)
            dao.deleteMessagesForSession(sessionId)
            if (currentSessionId == sessionId) {
                currentSessionId = null
                _messages.value = emptyList()
            }
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val chat = chatSession ?: return
        val sessionId = currentSessionId ?: return

        val userMsg = ChatMessage(text = userText, isUser = true, hasTyped = true)
        _messages.value = _messages.value + userMsg

        // Update session title if it's the first user message
        if (_messages.value.count { it.isUser } == 1) {
            viewModelScope.launch {
                val title = if (userText.length > 20) userText.take(20) + "..." else userText
                dao.insertSession(AiChatSession(id = sessionId, title = title))
            }
        }

        viewModelScope.launch {
            dao.insertMessage(AiChatMessage(id = userMsg.id, sessionId = sessionId, text = userMsg.text, isUser = userMsg.isUser))
            
            _isLoading.value = true
            try {
                val response = chat.sendMessage(userText)
                val aiText = response.text ?: "..."
                val aiMsg = ChatMessage(text = aiText, isUser = false)
                _messages.value = _messages.value + aiMsg
                
                dao.insertMessage(AiChatMessage(id = aiMsg.id, sessionId = sessionId, text = aiMsg.text, isUser = aiMsg.isUser))
            } catch (e: Exception) {
                val errorMsgText = if (e.localizedMessage?.contains("quota", ignoreCase = true) == true || e.localizedMessage?.contains("429") == true) {
                    "OUT OF LIMIT"
                } else {
                    "CONNECTION ERROR"
                }
                val errorMsg = ChatMessage(text = errorMsgText, isUser = false, isError = true, hasTyped = true)
                _messages.value = _messages.value + errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class AiChatViewModelFactory(private val dao: AiChatDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AiChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AiChatViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

