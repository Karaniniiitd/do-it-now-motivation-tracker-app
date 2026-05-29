package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.model.xpToLevelInfo
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel

@Composable
fun CharacterScreen(viewModel: GoalViewModel) {
    val userStats      by viewModel.userStats.collectAsState()
    val completedCount by viewModel.completedGoalCount.collectAsState()
    val currentStreak  by viewModel.currentStreak.collectAsState()

    val level = userStats.level
    val xp    = userStats.totalXp
    val (_, _, xpRange) = xpToLevelInfo(xp)

    // Perks unlocked per level
    val perks = buildList {
        if (level >= 1) add("GOAL TRACKER UNLOCKED")
        if (level >= 2) add("DAILY QUESTS UNLOCKED")
        if (level >= 3) add("STREAK GUARD ACTIVE")
        if (level >= 4) add("BOSS MISSIONS UNLOCKED")
        if (level >= 5) add("LEGEND STATUS ACHIEVED")
    }

    val lockedPerks = buildList {
        if (level < 2) add("LV2 DAILY QUESTS")
        if (level < 3) add("LV3 STREAK GUARD")
        if (level < 4) add("LV4 BOSS MISSIONS")
        if (level < 5) add("LV5 LEGEND STATUS")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(44.dp))

        Text(
            "CHARACTER",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize   = 26.sp,
            fontFamily = PixelFontFamily,
            letterSpacing = 2.sp
        )

        // ── Pixel character sprite ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PixelCharacterSprite(level = level, modifier = Modifier.size(120.dp))

                Text(
                    userStats.levelTitle.uppercase(),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 20.sp,
                    fontFamily = PixelFontFamily,
                    textAlign  = TextAlign.Center
                )
                Text(
                    "LEVEL $level",
                    color      = Color(0xFF888888),
                    fontSize   = 9.sp,
                    fontFamily = PixelFontFamily
                )
                Text(
                    "$xp XP  •  ${xpRange.first}/${xpRange.second} TO NEXT",
                    color      = Color(0xFF555555),
                    fontSize   = 7.sp,
                    fontFamily = PixelFontFamily
                )

                // XP bar
                val frac    = (xpRange.first.toFloat() / xpRange.second.toFloat()).coerceIn(0f, 1f)
                val filled  = (frac * 16).toInt()
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(16) { i ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .background(if (i < filled) Color.White else MaterialTheme.colorScheme.surface)
                        )
                    }
                }
            }
        }

        // ── Pixel stats row ───────────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CharStatBox(Modifier.weight(1f), "MISSIONS\nCOMPLETED", "$completedCount")
            CharStatBox(Modifier.weight(1f), "CURRENT\nSTREAK", "${currentStreak}D")
            CharStatBox(Modifier.weight(1f), "FREEZE\nTOKENS", "${userStats.freezeTokens}")
        }

        // ── Skill tree / unlocked perks ───────────────────────────
        Text(
            "SKILL TREE",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize   = 11.sp,
            fontFamily = PixelFontFamily,
            letterSpacing = 1.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            perks.forEach { perk ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pixel checkmark
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("X", color = MaterialTheme.colorScheme.background, fontSize = 7.sp, fontFamily = PixelFontFamily)
                    }
                    Text(
                        perk,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize   = 8.sp,
                        fontFamily = PixelFontFamily
                    )
                }
            }
            lockedPerks.forEach { perk ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF2A2A2A))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .border(1.dp, Color(0xFF333333)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?", color = Color(0xFF333333), fontSize = 7.sp, fontFamily = PixelFontFamily)
                    }
                    Text(
                        perk,
                        color      = Color(0xFF333333),
                        fontSize   = 8.sp,
                        fontFamily = PixelFontFamily
                    )
                }
            }
        }

        // ── Freeze token usage ────────────────────────────────────
        if (userStats.freezeTokens > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "FREEZE TOKENS: ${userStats.freezeTokens}",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize   = 10.sp,
                        fontFamily = PixelFontFamily
                    )
                    Text(
                        "SPEND 1 TOKEN TO PROTECT YOUR STREAK ON A MISSED DAY.",
                        color      = Color(0xFF888888),
                        fontSize   = 7.sp,
                        fontFamily = PixelFontFamily,
                        lineHeight = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF555555))
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { viewModel.spendFreezeToken() }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "USE FREEZE TOKEN",
                            color      = Color(0xFF888888),
                            fontSize   = 9.sp,
                            fontFamily = PixelFontFamily
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

// ── Pixel character sprite (canvas) ───────────────────────────────

@Composable
private fun PixelCharacterSprite(level: Int, modifier: Modifier) {
    // 8×16 pixel grids — different per level
    // 1=pixel on (white), 0=off, 2=accent (gray)
    val grids = mapOf(
        1 to listOf( // Rookie — simple humanoid
            "00111100",
            "00111100",
            "01111110",
            "01011010",
            "01111110",
            "00111100",
            "01111110",
            "11111111",
            "01111110",
            "01111110",
            "00100100",
            "00100100",
            "01100110",
            "01100110",
            "01100110",
            "00000000"
        ),
        2 to listOf( // Apprentice — add shoulder pads
            "01111110",
            "01111110",
            "11111111",
            "11011011",
            "11111111",
            "01111110",
            "11111111",
            "11111111",
            "01111110",
            "01111110",
            "00100100",
            "00100100",
            "01100110",
            "01100110",
            "01000010",
            "00000000"
        ),
        3 to listOf( // Challenger — armour
            "01111110",
            "11111111",
            "11111111",
            "11011011",
            "11111111",
            "01111110",
            "11111111",
            "11111111",
            "11111111",
            "01111110",
            "01111110",
            "01111110",
            "01111110",
            "01000010",
            "01000010",
            "00000000"
        ),
        4 to listOf( // Expert — cape
            "00111100",
            "01111110",
            "11111111",
            "11011011",
            "11111111",
            "01111110",
            "11111111",
            "11111111",
            "11111111",
            "11111111",
            "11011011",
            "11011011",
            "10011001",
            "10000001",
            "10000001",
            "00000000"
        ),
        5 to listOf( // Legend — crown + full armour
            "01010100",
            "01111110",
            "11111111",
            "11111111",
            "11011011",
            "11111111",
            "11111111",
            "11111111",
            "11111111",
            "11111111",
            "11111111",
            "11111111",
            "11011011",
            "11011011",
            "10000001",
            "00000000"
        )
    )

    val grid = grids[level.coerceIn(1, 5)] ?: grids[1]!!

    Canvas(modifier) {
        val rows    = grid.size
        val cols    = grid[0].length
        val cellW   = size.width / cols
        val cellH   = size.height / rows

        grid.forEachIndexed { row, line ->
            line.forEachIndexed { col, ch ->
                val color = when (ch) {
                    '1' -> Color.White
                    '2' -> Color(0xFF888888)
                    else -> null
                }
                if (color != null) {
                    drawRect(
                        color   = color,
                        topLeft = Offset(col * cellW, row * cellH),
                        size    = Size(cellW - 1f, cellH - 1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CharStatBox(modifier: Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .border(2.dp, MaterialTheme.colorScheme.primary)
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontFamily = PixelFontFamily)
            Text(label, color = Color(0xFF555555), fontSize = 6.sp, fontFamily = PixelFontFamily, lineHeight = 10.sp)
        }
    }
}
