package com.karan.do_it_now_motivation_tracker.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.karan.do_it_now_motivation_tracker.ui.theme.PixelFontFamily
import com.karan.do_it_now_motivation_tracker.util.FirebaseManager
import com.karan.do_it_now_motivation_tracker.viewmodel.GoalViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: GoalViewModel,
    onLoginSuccess: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var error   by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            scope.launch {
                loading = true
                error   = ""
                try {
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account?.idToken ?: throw Exception("No ID token")
                    val signInResult = FirebaseManager.handleSignInResult(idToken)
                    if (signInResult.isSuccess) {
                        // Restore cloud data
                        viewModel.restoreFromCloud()
                        onLoginSuccess()
                    } else {
                        error = "SIGN IN FAILED. TRY AGAIN."
                    }
                } catch (e: Exception) {
                    error = "ERROR: ${e.message?.take(40)?.uppercase() ?: "UNKNOWN"}"
                } finally {
                    loading = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(28.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // App Title
            Text(
                "DO IT\nNOW",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize   = 48.sp,
                fontFamily = PixelFontFamily,
                textAlign  = TextAlign.Center,
                lineHeight = 64.sp
            )

            Text(
                "MOTIVATION TRACKER",
                color      = Color(0xFF444444),
                fontSize   = 8.sp,
                fontFamily = PixelFontFamily,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(12.dp))

            // Info box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF333333))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "SYNC GOALS ACROSS DEVICES",
                        "NEVER LOSE YOUR PROGRESS",
                        "APPEAR ON GLOBAL LEADERBOARD"
                    ).forEach { benefit ->
                        Text(
                            "[ ] $benefit",
                            color      = Color(0xFF666666),
                            fontSize   = 7.sp,
                            fontFamily = PixelFontFamily
                        )
                    }
                }
            }

            // Google Sign In button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(enabled = !loading) {
                        val intent = FirebaseManager.getSignInIntent(context)
                        launcher.launch(intent)
                    }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "SIGN IN WITH GOOGLE",
                        color = MaterialTheme.colorScheme.background,
                        fontSize   = 11.sp,
                        fontFamily = PixelFontFamily,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Error message
            if (error.isNotEmpty()) {
                Text(
                    error,
                    color      = Color(0xFF888888),
                    fontSize   = 7.sp,
                    fontFamily = PixelFontFamily,
                    textAlign  = TextAlign.Center
                )
            }

            // Skip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF333333))
                    .clickable(enabled = !loading) { onSkip() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "CONTINUE WITHOUT ACCOUNT",
                    color      = Color(0xFF444444),
                    fontSize   = 8.sp,
                    fontFamily = PixelFontFamily
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}
