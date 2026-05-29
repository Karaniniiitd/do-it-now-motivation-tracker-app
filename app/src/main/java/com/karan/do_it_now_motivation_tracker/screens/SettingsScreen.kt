package com.karan.do_it_now_motivation_tracker.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.karan.do_it_now_motivation_tracker.ui.components.PixelIconGear
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.util.FirebaseManager
import com.karan.do_it_now_motivation_tracker.util.UserPrefsManager
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel

@Composable
fun SettingsScreen(navController: NavController, viewModel: GoalViewModel) {
    val context = LocalContext.current
    val prefs = remember { UserPrefsManager.getInstance(context) }
    
    val currentTheme by prefs.themeModeFlow.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var showLogOutDialog by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(FirebaseManager.isLoggedIn) }
    
    var userName by remember { mutableStateOf(prefs.userName) }
    var draftUserName by remember { mutableStateOf(prefs.userName) }
    var showNameChangeDialog by remember { mutableStateOf(false) }

    // ── Log Out Dialog ───────────────────────────────────────────────────
    if (showLogOutDialog) {
        AlertDialog(
            onDismissRequest = { showLogOutDialog = false },
            containerColor   = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "LOG OUT",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize = 13.sp
                )
            },
            text = {
                Text(
                    "ARE YOU SURE YOU WANT TO LOG OUT?",
                    color = Color(0xFF888888),
                    fontFamily = PixelFontFamily,
                    fontSize = 9.sp,
                    lineHeight = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseManager.signOut(context)
                    isLoggedIn = FirebaseManager.isLoggedIn
                    showLogOutDialog = false
                    Toast.makeText(context, "LOGGED OUT SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                }) {
                    Text("CONFIRM", color = Color(0xFFFF4444), fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogOutDialog = false }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            }
        )
    }

    // ── Name Change Dialog ────────────────────────────────────────────────
    if (showNameChangeDialog) {
        AlertDialog(
            onDismissRequest = { showNameChangeDialog = false },
            containerColor   = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "UPDATE DISPLAY NAME",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize = 13.sp
                )
            },
            text = {
                Text(
                    "ARE YOU SURE YOU WANT TO SAVE THIS NEW DISPLAY NAME?",
                    color = Color(0xFF888888),
                    fontFamily = PixelFontFamily,
                    fontSize = 9.sp,
                    lineHeight = 16.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    prefs.userName = draftUserName
                    userName = draftUserName
                    showNameChangeDialog = false
                    Toast.makeText(context, "NAME UPDATED", Toast.LENGTH_SHORT).show()
                }) {
                    Text("CONFIRM", color = MaterialTheme.colorScheme.primary, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameChangeDialog = false }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            }
        )
    }

    // ── Clear Data Dialog ────────────────────────────────────────────────
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor   = Color.Black,
            title = {
                Text(
                    "CLEAR ALL DATA?",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize   = 13.sp
                )
            },
            text = {
                Text(
                    "This will delete all goals and stats.",
                    color      = Color(0xFF888888),
                    fontFamily = PixelFontFamily,
                    fontSize   = 9.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                    showClearDialog = false
                }) {
                    Text("CONFIRM", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("CANCEL", color = Color(0xFF888888), fontFamily = PixelFontFamily, fontSize = 9.sp)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(44.dp))

        // ── Header ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "SETTINGS",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 24.sp,
                fontFamily = PixelFontFamily
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Text("X", color = MaterialTheme.colorScheme.onBackground, fontFamily = PixelFontFamily, fontSize = 16.sp)
            }
        }
        
        Spacer(Modifier.height(10.dp))

        // ── USER SETTINGS ─────────────────────────────────────────────────
        Text(
            "USER PROFILE",
            color = Color(0xFF888888),
            fontSize = 9.sp,
            fontFamily = PixelFontFamily,
            letterSpacing = 1.sp
        )

        // Change Name
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .background(MaterialTheme.colorScheme.surface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "DISPLAY NAME",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 8.sp,
                fontFamily = PixelFontFamily
            )
            BasicTextField(
                value = draftUserName,
                onValueChange = {
                    draftUserName = it
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = PixelFontFamily,
                    fontSize = 12.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF444444))
                    .padding(12.dp)
            )

            if (draftUserName != userName) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            if (draftUserName.isNotBlank()) {
                                showNameChangeDialog = true
                            } else {
                                Toast.makeText(context, "NAME CANNOT BE EMPTY", Toast.LENGTH_SHORT).show()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "SAVE",
                        color = MaterialTheme.colorScheme.background,
                        fontSize   = 9.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
        
        Spacer(Modifier.height(10.dp))

        // ── APP SETTINGS ──────────────────────────────────────────────────
        Text(
            "APP PREFERENCES",
            color = Color(0xFF888888),
            fontSize = 9.sp,
            fontFamily = PixelFontFamily,
            letterSpacing = 1.sp
        )

        // Theme toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.primary)
                .background(MaterialTheme.colorScheme.background)
                .clickable {
                    val next = when (currentTheme) {
                        UserPrefsManager.ThemeMode.SYSTEM -> UserPrefsManager.ThemeMode.LIGHT
                        UserPrefsManager.ThemeMode.LIGHT -> UserPrefsManager.ThemeMode.DARK
                        UserPrefsManager.ThemeMode.DARK -> UserPrefsManager.ThemeMode.SYSTEM
                    }
                    prefs.themeMode = next
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("THEME: ${currentTheme.name}", color = MaterialTheme.colorScheme.onBackground, fontSize = 9.sp, fontFamily = PixelFontFamily, letterSpacing = 2.sp)
        }
        
        Spacer(Modifier.height(10.dp))

        // ── ACCOUNT & DATA ────────────────────────────────────────────────
        Text(
            "ACCOUNT & DATA",
            color = Color(0xFF888888),
            fontSize = 9.sp,
            fontFamily = PixelFontFamily,
            letterSpacing = 1.sp
        )

        // Firebase: Login status + Sign In/Out button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, if (isLoggedIn) Color(0xFF333333) else MaterialTheme.colorScheme.primary)
                .background(if (isLoggedIn) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary)
                .clickable {
                    if (isLoggedIn) {
                        showLogOutDialog = true
                    } else {
                        navController.navigate("login")
                    }
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (isLoggedIn) "SIGNED IN  ●  SIGN OUT" else "SIGN IN WITH GOOGLE",
                    color      = if (isLoggedIn) Color(0xFF555555) else MaterialTheme.colorScheme.background,
                    fontSize   = 9.sp,
                    fontFamily = PixelFontFamily,
                    letterSpacing = 1.sp
                )
                if (isLoggedIn) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        FirebaseManager.email.uppercase().take(28),
                        color = Color(0xFF333333), fontSize = 6.sp, fontFamily = PixelFontFamily
                    )
                }
            }
        }

        // Clear data button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF333333))
                .background(MaterialTheme.colorScheme.background)
                .clickable { showClearDialog = true }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "CLEAR ALL DATA",
                color      = Color(0xFFFF4444), // Made this red for danger
                fontSize   = 9.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 2.sp
            )
        }

        Spacer(Modifier.height(100.dp))
    }
}
