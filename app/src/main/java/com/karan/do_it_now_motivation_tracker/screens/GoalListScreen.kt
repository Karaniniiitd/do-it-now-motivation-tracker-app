package com.karan.do_it_now_motivation_tracker.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconTarget
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoalListScreen(navController: NavController, viewModel: GoalViewModel) {
    val activeGoals    by viewModel.activeGoals.collectAsState()
    val completedGoals by viewModel.completedGoals.collectAsState()
    val fmt = SimpleDateFormat("dd MMM", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item { Spacer(Modifier.height(48.dp)) }

        // ── Header ──────────────────────────────────────────────
        item {
            Text(
                "MISSIONS",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize   = 26.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "${activeGoals.size} ACTIVE  ${completedGoals.size} DONE",
                color      = Color(0xFF888888),
                fontSize   = 8.sp,
                fontFamily = PixelFontFamily
            )
            Spacer(Modifier.height(16.dp))
        }

        // ── Active missions ─────────────────────────────────────
        if (activeGoals.isNotEmpty()) {
            item {
                PixelSectionHeader("ACTIVE")
            }
            items(activeGoals) { goal ->
                Column {
                    GoalRowCard(
                        goal = goal,
                        onClick = { navController.navigate("editGoal/${goal.id}") }
                    )
                    Spacer(Modifier.height(2.dp))
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFF333333))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        PixelIconTarget(Modifier.size(40.dp), Color(0xFF444444))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "NO ACTIVE MISSIONS",
                            color      = Color(0xFF555555),
                            fontSize   = 9.sp,
                            fontFamily = PixelFontFamily,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "TAP + TO ADD ONE",
                            color      = Color(0xFF444444),
                            fontSize   = 8.sp,
                            fontFamily = PixelFontFamily
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // ── Completed missions ──────────────────────────────────
        if (completedGoals.isNotEmpty()) {
            item {
                PixelSectionHeader("COMPLETED")
            }
            items(completedGoals) { goal ->
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color(0xFF333333))
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { navController.navigate("editGoal/${goal.id}") }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // White square check mark
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .border(1.dp, Color(0xFF444444))
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "X",
                                    color      = Color(0xFF444444),
                                    fontSize   = 9.sp,
                                    fontFamily = PixelFontFamily
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    goal.title.uppercase(),
                                    color      = Color(0xFF555555),
                                    fontSize   = 9.sp,
                                    fontFamily = PixelFontFamily,
                                    maxLines   = 1,
                                    overflow   = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                val completedStr = goal.completedDate?.let {
                                    "DONE ${fmt.format(Date(it))}"
                                } ?: "COMPLETED"
                                Text(
                                    "$completedStr  ${goal.difficulty.uppercase()}",
                                    color      = Color(0xFF444444),
                                    fontSize   = 7.sp,
                                    fontFamily = PixelFontFamily
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .border(1.dp, Color(0xFF444444))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    "DONE",
                                    color      = Color(0xFF555555),
                                    fontSize   = 7.sp,
                                    fontFamily = PixelFontFamily
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
private fun PixelSectionHeader(text: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(3.dp).height(16.dp).background(MaterialTheme.colorScheme.primary))
            Spacer(Modifier.width(8.dp))
            Text(
                text,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize      = 10.sp,
                fontFamily    = PixelFontFamily,
                letterSpacing = 2.sp
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}
