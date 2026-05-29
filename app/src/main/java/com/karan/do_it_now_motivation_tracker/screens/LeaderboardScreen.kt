package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.util.FirebaseManager
import com.karan.do_it_now_motivation_tracker.util.RealtimeDatabaseSync

@Composable
fun LeaderboardScreen(
    currentUid: String?,
    currentStreak: Int,
    currentScore: Int,
    currentLevel: Int,
    onRefresh: () -> Unit = {}
) {
    var entries  by remember { mutableStateOf<List<RealtimeDatabaseSync.LeaderboardEntry>>(emptyList()) }
    var loading  by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        onRefresh()
        kotlinx.coroutines.delay(500) // Give Firebase a moment to sync
        entries = RealtimeDatabaseSync.downloadLeaderboard()
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(44.dp))

        Text(
            "GLOBAL\nLEADERBOARD",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize      = 26.sp,
            fontFamily    = PixelFontFamily,
            lineHeight    = 38.sp,
            letterSpacing = 2.sp
        )

        Text(
            "TOP DISCIPLINE SCORES WORLDWIDE",
            color      = Color(0xFF555555),
            fontSize   = 7.sp,
            fontFamily = PixelFontFamily
        )

        // Column headers
        Row(
            Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF333333))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("RANK", "NAME", "SCORE", "LV", "STREAK").forEach { h ->
                Text(h, color = Color(0xFF444444), fontSize = 6.sp, fontFamily = PixelFontFamily)
            }
        }

        if (loading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, strokeWidth = 2.dp)
            }
        } else if (entries.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().height(200.dp).border(1.dp, MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "NO ENTRIES YET.\nBE THE FIRST!",
                    color = Color(0xFF333333), fontSize = 9.sp, fontFamily = PixelFontFamily,
                    textAlign = TextAlign.Center, lineHeight = 16.sp
                )
            }
        } else {
            entries.forEachIndexed { index, entry ->
                val isMe      = entry.uid == currentUid
                val rank      = index + 1
                val borderCol = when {
                    isMe       -> Color.White
                    rank == 1  -> Color.White
                    rank == 2  -> Color(0xFFCCCCCC)
                    rank == 3  -> Color(0xFF888888)
                    else       -> MaterialTheme.colorScheme.surface
                }
                val textCol   = if (isMe) Color.Black else Color.White
                val bgCol     = if (isMe) Color.White else Color.Black

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(if (isMe) 2.dp else 1.dp, borderCol)
                        .background(bgCol)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    val rankLabel = when (rank) {
                        1 -> "#1"
                        2 -> "#2"
                        3 -> "#3"
                        else -> "#$rank"
                    }
                    Text(rankLabel,
                        color = textCol, fontSize = 9.sp, fontFamily = PixelFontFamily)
                    Text(
                        entry.displayName.uppercase().take(10),
                        color = textCol, fontSize = 7.sp, fontFamily = PixelFontFamily
                    )
                    Text("${entry.disciplineScore}",
                        color = textCol, fontSize = 9.sp, fontFamily = PixelFontFamily)
                    Text("LV${entry.level}",
                        color = textCol, fontSize = 7.sp, fontFamily = PixelFontFamily)
                    Text("${entry.streak}D",
                        color = textCol, fontSize = 7.sp, fontFamily = PixelFontFamily)
                }
            }
        }

        // "YOU" row if not in top 50
        if (!loading && currentUid != null && entries.none { it.uid == currentUid }) {
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF333333))
                    .padding(12.dp)
            ) {
                Text(
                    "YOU ARE NOT IN TOP 50 YET. SCORE: $currentScore",
                    color = Color(0xFF555555), fontSize = 7.sp, fontFamily = PixelFontFamily
                )
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}
