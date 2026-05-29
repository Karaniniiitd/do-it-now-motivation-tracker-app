package com.karan.do_it_now_motivation_tracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.android.awaitFrame
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ── Data ──────────────────────────────────────────────────────────

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,          // velocity x  px/frame
    var vy: Float,          // velocity y  px/frame
    var life: Float = 1f,   // 1.0 = alive, 0.0 = dead
    val size: Float,        // pixel square side length
    val color: Color
)

// ── Burst factory ─────────────────────────────────────────────────

fun burstParticles(
    centerX: Float,
    centerY: Float,
    count: Int = 40,
    colors: List<Color> = listOf(Color.White, Color(0xFFCCCCCC), Color(0xFF888888))
): List<Particle> = (0 until count).map {
    val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
    val speed = Random.nextFloat() * 14f + 4f
    Particle(
        x     = centerX,
        y     = centerY,
        vx    = cos(angle) * speed,
        vy    = sin(angle) * speed,
        life  = 1f,
        size  = Random.nextFloat() * 8f + 3f,
        color = colors[Random.nextInt(colors.size)]
    )
}

// ── Composable ────────────────────────────────────────────────────

/**
 * Drop this over any layout to show pixel particle bursts.
 * Usage:
 *   val particles = remember { mutableStateListOf<Particle>() }
 *   // trigger:  particles.addAll(burstParticles(cx, cy))
 *   ParticleOverlay(particles = particles, modifier = Modifier.fillMaxSize())
 */
@Composable
fun ParticleOverlay(
    particles: androidx.compose.runtime.snapshots.SnapshotStateList<Particle>,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        while (true) {
            awaitFrame()
            if (particles.isEmpty()) continue
            val toRemove = mutableListOf<Particle>()
            particles.forEach { p ->
                p.x    += p.vx
                p.y    += p.vy
                p.vy   += 0.6f     // gravity
                p.vx   *= 0.96f    // horizontal friction
                p.life -= 0.022f   // fade ~45 frames
                if (p.life <= 0f) toRemove.add(p)
            }
            particles.removeAll(toRemove)
        }
    }

    Canvas(modifier) {
        particles.forEach { p ->
            drawRect(
                color   = p.color.copy(alpha = p.life.coerceIn(0f, 1f)),
                topLeft = Offset(p.x - p.size / 2f, p.y - p.size / 2f),
                size    = Size(p.size, p.size)
            )
        }
    }
}
