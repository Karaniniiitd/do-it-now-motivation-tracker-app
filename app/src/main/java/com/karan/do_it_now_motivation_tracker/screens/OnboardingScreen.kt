package com.karan.do_it_now_motivation_tracker.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.ui.components.Categories
import com.karan.do_it_now_motivation_tracker.ui.components.PixelCategoryIcon
import com.karan.do_it_now_motivation_tracker.ui.components.PixelDifficultyIcon
import com.karan.do_it_now_motivation_tracker.util.UserPrefsManager
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily

private data class DiffOption(val label: String, val key: String)

private val diffOptions = listOf(
    DiffOption("EASY",   "Easy"),
    DiffOption("MEDIUM", "Medium"),
    DiffOption("HARD",   "Hard"),
    DiffOption("BOSS",   "Boss")
)

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs   = remember { UserPrefsManager.getInstance(context) }

    var name               by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("Easy") }
    var selectedCategory   by remember { mutableStateOf(Categories.all.first()) }

    // Cursor blink
    val inf   = rememberInfiniteTransition(label = "blink")
    val blink by inf.animateFloat(0f, 1f,
        infiniteRepeatable(tween(500), RepeatMode.Reverse), "blink")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Spacer(Modifier.height(40.dp))

            // ── Title ─────────────────────────────────────────────
            Text(
                "CREATE\nCHARACTER",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize   = 36.sp,
                fontFamily = PixelFontFamily,
                lineHeight = 48.sp,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth()
            )

            // ── Goal Name ─────────────────────────────────────────
            BasicTextField(
                value         = name,
                onValueChange = { name = it.take(30) },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize   = 13.sp
                ),
                cursorBrush = SolidColor(Color.White),
                singleLine  = false,
                maxLines    = 2,
                decorationBox = { inner ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .border(2.dp, MaterialTheme.colorScheme.primary)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        if (name.isEmpty()) {
                            Text(
                                "USERNAME",
                                color      = Color(0xFF444444),
                                fontFamily = PixelFontFamily,
                                fontSize   = 13.sp
                            )
                        }
                        inner()
                    }
                }
            )

            // ── Difficulty ────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "DIFFICULTY",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 12.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    diffOptions.forEach { diff ->
                        val sel = selectedDifficulty == diff.key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .border(2.dp, MaterialTheme.colorScheme.primary)
                                .background(if (sel) Color.White else Color.Black)
                                .clickable { selectedDifficulty = diff.key }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PixelDifficultyIcon(
                                    difficulty = diff.key,
                                    modifier   = Modifier.size(28.dp),
                                    color      = if (sel) Color.Black else Color.White
                                )
                                Text(
                                    diff.label,
                                    color      = if (sel) Color.Black else Color.White,
                                    fontSize   = 7.sp,
                                    fontFamily = PixelFontFamily,
                                    textAlign  = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // ── Choose Category ───────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "CHOOSE CATEGORY",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize   = 12.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .horizontalScroll(rememberScrollState())
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Categories.all.forEach { cat ->
                        val sel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .background(if (sel) Color.White else Color.Black)
                                .border(if (sel) 0.dp else 1.dp, Color(0xFF444444))
                                .clickable { selectedCategory = cat }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PixelCategoryIcon(
                                    category = cat,
                                    modifier = Modifier.size(32.dp),
                                    color    = if (sel) Color.Black else Color.White
                                )
                                Text(
                                    Categories.label(cat),
                                    color      = if (sel) Color.Black else Color.White,
                                    fontSize   = 7.sp,
                                    fontFamily = PixelFontFamily,
                                    textAlign  = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── INITIATE ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        val finalName = name.trim().uppercase().ifBlank { "WARRIOR" }
                        prefs.userName     = finalName
                        prefs.userClass    = selectedDifficulty
                        prefs.hasOnboarded = true
                        navController.navigate("dashboard") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "INITIATE",
                    color = MaterialTheme.colorScheme.background,
                    fontSize   = 18.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 2.sp
                )
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}
