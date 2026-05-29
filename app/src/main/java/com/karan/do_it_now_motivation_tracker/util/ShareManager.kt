package com.karan.do_it_now_motivation_tracker.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Renders a stats card as a Bitmap and shares it via Android share sheet.
 * No Compose capture needed — draws directly with Canvas + Paint so it
 * always works regardless of screen state.
 */
object ShareManager {

    fun shareStats(
        context: Context,
        userName: String,
        level: Int,
        levelTitle: String,
        totalXp: Int,
        currentStreak: Int,
        completedGoals: Int,
        totalGoals: Int
    ) {
        val bitmap = renderStatCard(
            userName       = userName,
            level          = level,
            levelTitle     = levelTitle,
            totalXp        = totalXp,
            currentStreak  = currentStreak,
            completedGoals = completedGoals,
            totalGoals     = totalGoals
        )

        // Save to cache
        val dir  = File(context.cacheDir, "images").also { it.mkdirs() }
        val file = File(dir, "stats_card.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type      = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "MY DO IT NOW STATS\nLEVEL $level $levelTitle • ${currentStreak}D STREAK • $totalXp XP")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Stats"))
    }

    private fun renderStatCard(
        userName: String,
        level: Int,
        levelTitle: String,
        totalXp: Int,
        currentStreak: Int,
        completedGoals: Int,
        totalGoals: Int
    ): Bitmap {
        val W = 800; val H = 400
        val bitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        canvas.drawColor(Color.BLACK)

        val paintW = Paint().apply {
            color     = Color.WHITE
            textSize  = 28f
            typeface  = Typeface.MONOSPACE
            isAntiAlias = false  // pixel-crisp
        }
        val paintG = Paint(paintW).apply { color = Color.argb(255, 136, 136, 136) }
        val paintD = Paint(paintW).apply { color = Color.argb(255, 51, 51, 51) }

        // Border
        val borderPaint = Paint().apply { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 4f }
        canvas.drawRect(4f, 4f, W - 4f, H - 4f, borderPaint)

        // App tag
        paintD.textSize = 18f
        canvas.drawText("DO IT NOW", 40f, 50f, paintD)

        // Name
        paintW.textSize = 32f
        canvas.drawText("@${userName.uppercase()}", 40f, 100f, paintW)

        // Divider
        val divPaint = Paint().apply { color = Color.argb(255, 40, 40, 40) }
        canvas.drawRect(40f, 116f, W - 40f, 120f, divPaint)

        // Level
        paintW.textSize = 64f
        canvas.drawText("LV$level", 40f, 200f, paintW)
        paintG.textSize = 24f
        canvas.drawText(levelTitle.uppercase(), 40f, 230f, paintG)

        // Stats grid (right side)
        val statPaintL = Paint(paintG).apply { textSize = 18f }
        val statPaintV = Paint(paintW).apply { textSize = 28f }

        fun drawStat(label: String, value: String, x: Float, y: Float) {
            canvas.drawText(label, x, y, statPaintL)
            canvas.drawText(value, x, y + 36f, statPaintV)
        }

        drawStat("STREAK",         "${currentStreak}D",     480f, 140f)
        drawStat("TOTAL XP",       "$totalXp",              480f, 220f)
        drawStat("MISSIONS DONE",  "$completedGoals/$totalGoals", 480f, 300f)

        // XP bar
        val barY   = 330f
        val barW   = W - 80f
        val frac   = (totalXp % 300).toFloat() / 300f  // simplified
        canvas.drawRect(40f, barY, 40f + barW, barY + 12f, divPaint)
        canvas.drawRect(40f, barY, 40f + barW * frac, barY + 12f, Paint().apply { color = Color.WHITE })

        // Footer
        paintD.textSize = 16f
        canvas.drawText("BUILT WITH DO IT NOW  •  MOTIVATION TRACKER", 40f, H - 30f, paintD)

        return bitmap
    }
}
