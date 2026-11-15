package com.kboard.thekami.ime.feedback

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key

/**
 * Gives each kind of key its own vibration: a light tick for characters,
 * softer for space, sharper for backspace and firmest for enter, and a soft
 * confirmation for a chosen suggestion. The values are the Pulse constants
 * below so they can be tuned on a real device.
 */
internal class KeyHaptics(context: Context) {
    private val vibrator: Vibrator? =
        context.getSystemService(Vibrator::class.java)?.takeIf { it.hasVibrator() }
    private val hasAmplitudeControl = vibrator?.hasAmplitudeControl() == true

    fun perform(key: Key) {
        pulseFor(key)?.let(::vibrate)
    }

    fun suggestionSelected() = vibrate(SUGGESTION)

    private fun vibrate(pulse: Pulse) {
        val device = vibrator ?: return
        val amplitude = if (hasAmplitudeControl) pulse.amplitude else VibrationEffect.DEFAULT_AMPLITUDE
        device.vibrate(VibrationEffect.createOneShot(pulse.durationMs, amplitude))
    }

    private fun pulseFor(key: Key): Pulse? = when (key) {
        is Key.Character -> CHARACTER
        is Key.Control -> pulseFor(key.action)
        is Key.Spacer -> null
    }

    private fun pulseFor(action: ControlAction): Pulse = when (action) {
        ControlAction.SPACE -> SPACE
        ControlAction.BACKSPACE -> BACKSPACE
        ControlAction.ENTER -> ENTER
        ControlAction.SHIFT,
        ControlAction.SWITCH_LANGUAGE,
        ControlAction.SHOW_LETTERS,
        ControlAction.SHOW_SYMBOLS,
        ControlAction.SHOW_MORE_SYMBOLS,
        -> MODIFIER
    }

    /** One vibration: [durationMs] long, [amplitude] from 1 (weakest) to 255. */
    private class Pulse(val durationMs: Long, val amplitude: Int)

    private companion object {
        val CHARACTER = Pulse(durationMs = 12, amplitude = 60)
        val MODIFIER = Pulse(durationMs = 14, amplitude = 80)
        val SPACE = Pulse(durationMs = 16, amplitude = 50)
        val BACKSPACE = Pulse(durationMs = 20, amplitude = 110)
        val ENTER = Pulse(durationMs = 26, amplitude = 150)
        val SUGGESTION = Pulse(durationMs = 10, amplitude = 40)
    }
}
