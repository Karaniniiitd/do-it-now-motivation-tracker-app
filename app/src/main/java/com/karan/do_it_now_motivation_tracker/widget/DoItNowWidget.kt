package com.karan.do_it_now_motivation_tracker.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontFamily
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.color.ColorProviders
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.do_it_now_motivation_tracker.data.AppDatabase
import com.karan.do_it_now_motivation_tracker.util.UserPrefsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DoItNowWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db      = AppDatabase.getDatabase(context)
        val prefs   = UserPrefsManager.getInstance(context)
        val fmt     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today   = fmt.format(Date())

        // Read data
        val streak      = try {
            val logs = db.dailyLogDao().getAllLogsOnce()
            calculateStreak(logs.map { it.date }, fmt)
        } catch (_: Exception) { 0 }

        val stats = try { db.userStatsDao().getUserStatsOnce() } catch (_: Exception) { null }
        val focus = try { db.goalDao().getActiveGoalsOnce().minByOrNull { it.endDate } } catch (_: Exception) { null }
        val userName = prefs.userName.ifBlank { "WARRIOR" }

        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(12.dp)
                ) {
                    Column(
                        modifier              = GlanceModifier.fillMaxSize(),
                        verticalAlignment     = Alignment.Vertical.Top,
                        horizontalAlignment   = Alignment.Horizontal.Start
                    ) {
                        // App name
                        Text(
                            "DO IT NOW",
                            style = TextStyle(
                                color    = ColorProvider(Color.White),
                                fontSize = 8.sp
                            )
                        )

                        Spacer(GlanceModifier.height(8.dp))

                        // Hello
                        Text(
                            "HI, ${userName.uppercase()}",
                            style = TextStyle(
                                color    = ColorProvider(Color.White),
                                fontSize = 12.sp
                            )
                        )

                        Spacer(GlanceModifier.height(4.dp))

                        // Streak
                        Row {
                            Text(
                                "STREAK  ",
                                style = TextStyle(color = ColorProvider(Color(0xFF888888)), fontSize = 7.sp)
                            )
                            Text(
                                "${streak}D",
                                style = TextStyle(color = ColorProvider(Color.White), fontSize = 7.sp)
                            )
                        }

                        // Level
                        stats?.let {
                            Row {
                                Text(
                                    "LEVEL  ",
                                    style = TextStyle(color = ColorProvider(Color(0xFF888888)), fontSize = 7.sp)
                                )
                                Text(
                                    "LV${it.level}  ${it.levelTitle.uppercase()}",
                                    style = TextStyle(color = ColorProvider(Color.White), fontSize = 7.sp)
                                )
                            }
                        }

                        Spacer(GlanceModifier.height(8.dp))

                        // Today's focus
                        focus?.let {
                            Text(
                                "FOCUS",
                                style = TextStyle(color = ColorProvider(Color(0xFF555555)), fontSize = 6.sp)
                            )
                            Text(
                                it.title.uppercase().take(20),
                                style = TextStyle(color = ColorProvider(Color.White), fontSize = 8.sp)
                            )
                        } ?: Text(
                            "NO ACTIVE MISSIONS",
                            style = TextStyle(color = ColorProvider(Color(0xFF444444)), fontSize = 7.sp)
                        )
                    }
                }
            }
        }
    }

    private fun calculateStreak(dates: List<String>, fmt: SimpleDateFormat): Int {
        val cal = java.util.Calendar.getInstance()
        var streak = 0
        while (true) {
            val dateStr = fmt.format(cal.time)
            if (dates.contains(dateStr)) {
                streak++
                cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            } else break
        }
        return streak
    }
}

class DoItNowWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = DoItNowWidget()
}
