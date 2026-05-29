package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.util.UserPrefsManager
import com.karan.do_it_now_motivation_tracker.viewmodel.AiChatViewModel
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import kotlinx.coroutines.launch

import com.karan.do_it_now_motivation_tracker.viewmodel.ChatMessage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    navController: NavController,
    goalViewModel: GoalViewModel,
    aiViewModel: AiChatViewModel
) {
    val context = LocalContext.current
    val prefs = remember { UserPrefsManager.getInstance(context) }
    
    val userStats by goalViewModel.userStats.collectAsState()
    val activeGoals by goalViewModel.activeGoals.collectAsState()
    val messages by aiViewModel.messages.collectAsState()
    val sessions by aiViewModel.sessions.collectAsState()
    val isLoading by aiViewModel.isLoading.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val sheetState = rememberModalBottomSheetState()
    var showHistorySheet by remember { mutableStateOf(false) }

    // Initialize the AI context when the screen is first loaded
    LaunchedEffect(Unit) {
        aiViewModel.initialize(
            userName = prefs.userName,
            stats = userStats,
            activeGoals = activeGoals
        )
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "SENSEI",
                color = MaterialTheme.colorScheme.background,
                fontFamily = PixelFontFamily,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Chat History",
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showHistorySheet = true }
            )
        }

        // ── Chat Messages ────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
                val bgColor = if (message.isUser) MaterialTheme.colorScheme.primary else Color(0xFF333333)
                val textColor = if (message.isUser) MaterialTheme.colorScheme.background else Color.White
                val borderColor = if (message.isUser) Color.Transparent else MaterialTheme.colorScheme.primary

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = alignment
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f) // Max width for a bubble
                            .border(if (message.isUser) 0.dp else 2.dp, borderColor)
                            .background(if (message.isError) Color(0xFFAA0000) else bgColor)
                            .padding(12.dp)
                    ) {
                        if (message.isUser || message.isError) {
                            Text(
                                text = message.text,
                                color = textColor,
                                fontFamily = PixelFontFamily,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        } else {
                            TypewriterText(
                                message = message,
                                color = textColor,
                                fontFamily = PixelFontFamily,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "THINKING...",
                            color = Color.Gray,
                            fontFamily = PixelFontFamily,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // ── Input Field ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .weight(1f)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .padding(12.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(enabled = !isLoading && inputText.isNotBlank()) {
                        aiViewModel.sendMessage(inputText)
                        inputText = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    }

    var sessionToDelete by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            sheetState = sheetState,
            containerColor = Color.Black
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CHAT HISTORY",
                        color = Color.White,
                        fontFamily = PixelFontFamily,
                        fontSize = 18.sp
                    )
                    IconButton(
                        onClick = {
                            aiViewModel.startNewSession(prefs.userName)
                            coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) showHistorySheet = false
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "New Chat", tint = Color.White)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn {
                    items(sessions) { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    aiViewModel.loadSession(session.id)
                                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) showHistorySheet = false
                                    }
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = session.title,
                                    color = Color.White,
                                    fontFamily = PixelFontFamily,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(session.timestamp)),
                                    color = Color.Gray,
                                    fontFamily = PixelFontFamily,
                                    fontSize = 10.sp
                                )
                            }
                            IconButton(onClick = { sessionToDelete = session.id }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }

    if (sessionToDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text("DELETE CHAT", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 13.sp)
            },
            text = {
                Text("ARE YOU SURE YOU WANT TO DELETE THIS CHAT?", color = Color(0xFF888888), fontFamily = PixelFontFamily, fontSize = 9.sp)
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    aiViewModel.deleteSession(sessionToDelete!!)
                    sessionToDelete = null
                }) {
                    Text("DELETE", color = Color(0xFFFF4444), fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { sessionToDelete = null }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            }
        )
    }
}

@Composable
fun TypewriterText(
    message: ChatMessage,
    modifier: Modifier = Modifier,
    color: Color,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
    fontSize: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit
) {
    var textToDisplay by remember { mutableStateOf(if (message.hasTyped) message.text else "") }
    
    LaunchedEffect(message.text) {
        if (!message.hasTyped) {
            textToDisplay = ""
            for (i in message.text.indices) {
                textToDisplay += message.text[i]
                kotlinx.coroutines.delay(10) // Fast typing speed
            }
            message.hasTyped = true
        } else {
            textToDisplay = message.text
        }
    }
    
    Text(
        text = textToDisplay,
        modifier = modifier,
        color = color,
        fontFamily = fontFamily,
        fontSize = fontSize,
        lineHeight = lineHeight
    )
}
