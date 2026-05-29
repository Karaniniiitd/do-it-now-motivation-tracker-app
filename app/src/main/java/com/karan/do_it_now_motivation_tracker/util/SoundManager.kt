package com.karan.do_it_now_motivation_tracker.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Generates and plays 8-bit style square wave tones programmatically.
 * No audio files needed. Respects system media volume.
 */
object SoundManager {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var enabled = true

    fun setEnabled(on: Boolean) { enabled = on }
    fun isEnabled() = enabled

    /** Short coin / goal-complete sound */
    fun playComplete() = play(listOf(
        880 to 80L,
        1047 to 80L,
        1319 to 120L
    ))

    /** Level-up fanfare */
    fun playLevelUp() = play(listOf(
        523  to 100L,
        659  to 100L,
        784  to 100L,
        1047 to 250L
    ))

    /** Quest complete chime */
    fun playQuestComplete() = play(listOf(
        784 to 80L,
        880 to 80L,
        1047 to 150L
    ))

    /** Error / deny beep */
    fun playError() = play(listOf(
        220 to 150L
    ))

    // ── Internal ─────────────────────────────────────────────────

    private fun play(notes: List<Pair<Int, Long>>) {
        if (!enabled) return
        scope.launch {
            notes.forEach { (freq, durationMs) ->
                playTone(freq, durationMs)
            }
        }
    }

    private fun playTone(frequency: Int, durationMs: Long) {
        val sampleRate  = 22050
        val numSamples  = (sampleRate * durationMs / 1000).toInt()
        val buffer      = ShortArray(numSamples)
        val angularFreq = 2.0 * PI * frequency / sampleRate

        for (i in 0 until numSamples) {
            val t        = i.toDouble()
            val raw      = sin(angularFreq * t)
            // Square wave approximation (clamp sine)
            val square   = if (raw >= 0) 1.0 else -1.0
            // Fade out last 10% to avoid clicks
            val fade     = if (i > numSamples * 0.9) (numSamples - i).toDouble() / (numSamples * 0.1) else 1.0
            buffer[i]    = (square * fade * Short.MAX_VALUE * 0.3).toInt().toShort()
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        Thread.sleep(durationMs + 20)
        track.stop()
        track.release()
    }
}
