package com.karan.do_it_now_motivation_tracker.screens

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.ui.theme.AtmosphericBlack
import com.karan.do_it_now_motivation_tracker.ui.theme.CardBorder
import com.karan.do_it_now_motivation_tracker.ui.theme.CyanGlow
import com.karan.do_it_now_motivation_tracker.ui.theme.DimText
import com.karan.do_it_now_motivation_tracker.ui.theme.GlowOrange
import com.karan.do_it_now_motivation_tracker.ui.theme.MidnightCard
import com.karan.do_it_now_motivation_tracker.ui.theme.MutedPurple
import com.karan.do_it_now_motivation_tracker.ui.theme.MutedText
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftGreen
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftIndigo
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftRed
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftWhite
import com.karan.do_it_now_motivation_tracker.ui.theme.SoftYellow
import com.karan.do_it_now_motivation_tracker.ui.theme.WarmAmber
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel

@Composable
fun ProfileScreen(viewModel: GoalViewModel) {
    val context = LocalContext.current
    val totalGoals by viewModel.totalGoals.collectAsState()
    val completedCount by viewModel.completedGoalCount.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val longestStreak by viewModel.longestStreak.collectAsState()
    val activeGoals by viewModel.activeGoals.collectAsState()
    val completionRate = if (totalGoals > 0) (completedCount * 100) / totalGoals else 0

    var showDeleteDialog by remember { mutableStateOf(false) }

    val inf = rememberInfiniteTransition(label = "profile")
    val pulse by inf.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse), "pulse"
    )
    val ringRotate by inf.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(12000, easing = LinearEasing)), "ring"
    )

    Box(modifier = Modifier.fillMaxSize().background(AtmosphericBlack)) {
        // Ambient glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                Brush.radialGradient(
                    listOf(MutedPurple.copy(alpha = 0.05f + pulse * 0.03f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.2f),
                    radius = size.width * 0.6f
                ),
                radius = size.width * 0.6f,
                center = Offset(size.width * 0.5f, size.height * 0.2f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Animated ring avatar
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(110.dp)) {
                    val r = size.minDimension / 2f - 4.dp.toPx()
                    // Outer spinning dashed ring
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(CyanGlow.copy(alpha = 0.6f), SoftIndigo.copy(alpha = 0.2f), CyanGlow.copy(alpha = 0.6f))
                        ),
                        startAngle = ringRotate,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(SoftIndigo.copy(alpha = 0.3f), AtmosphericBlack)
                            )
                        )
                        .border(1.dp, CyanGlow.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✦", fontSize = 32.sp, color = CyanGlow.copy(alpha = 0.7f))
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "goal setter",
                style = MaterialTheme.typography.headlineMedium,
                color = SoftWhite.copy(alpha = 0.9f),
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "building discipline, one day at a time",
                style = MaterialTheme.typography.bodySmall,
                color = DimText,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(40.dp))

            // Stats grid
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AtmosphericStatCard(Modifier.weight(1f), "total", "$totalGoals", MutedPurple)
                AtmosphericStatCard(Modifier.weight(1f), "completed", "$completedCount", SoftGreen)
            }
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                AtmosphericStatCard(Modifier.weight(1f), "streak", "$currentStreak days", GlowOrange)
                AtmosphericStatCard(Modifier.weight(1f), "best", "$longestStreak days", WarmAmber)
            }

            Spacer(Modifier.height(20.dp))

            // Completion ring card
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(MidnightCard.copy(alpha = 0.4f))
                    .border(1.dp, CardBorder.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("completion rate", style = MaterialTheme.typography.labelSmall, color = DimText, letterSpacing = 1.5.sp)
                    Spacer(Modifier.height(12.dp))
                    Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(90.dp)) {
                            val stroke = 6.dp.toPx()
                            val r = size.minDimension / 2f - stroke
                            drawArc(CardBorder.copy(alpha = 0.2f), 0f, 360f, false, style = Stroke(stroke))
                            if (completionRate > 0) {
                                drawArc(
                                    Brush.sweepGradient(listOf(CyanGlow.copy(alpha = 0.8f), SoftIndigo.copy(alpha = 0.4f))),
                                    -90f, completionRate * 3.6f, false, style = Stroke(stroke)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$completionRate%",
                                style = MaterialTheme.typography.headlineMedium,
                                color = CyanGlow,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "$completedCount of $totalGoals goals · ${activeGoals.size} active",
                        style = MaterialTheme.typography.bodySmall,
                        color = DimText
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Clear data button
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftRed.copy(alpha = 0.08f),
                    contentColor = SoftRed.copy(alpha = 0.6f)
                )
            ) {
                Text("clear all data", style = MaterialTheme.typography.titleMedium, letterSpacing = 1.sp)
            }

            Spacer(Modifier.height(100.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("clear everything?", color = SoftWhite, letterSpacing = 1.sp) },
            text = { Text("this will permanently delete all goals and streak data.", color = MutedText) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData(); showDeleteDialog = false
                    Toast.makeText(context, "all data cleared", Toast.LENGTH_SHORT).show()
                }) { Text("delete", color = SoftRed.copy(alpha = 0.8f), letterSpacing = 1.sp) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("cancel", color = DimText, letterSpacing = 1.sp)
                }
            },
            containerColor = MidnightCard,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun AtmosphericStatCard(modifier: Modifier, label: String, value: String, glowColor: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MidnightCard.copy(alpha = 0.4f))
            .border(1.dp, glowColor.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = DimText, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = glowColor.copy(alpha = 0.85f))
        }
    }
}