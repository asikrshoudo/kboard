package com.kboard.thekami.ime.feedback

import android.content.Context
import android.media.AudioManager
import com.kboard.thekami.engine.layout.Key

/**
 * Plays the phone's own key click sounds. Android plays them only when
 * "Touch sounds" is on in the phone's Sound settings, and this class stays
 * quiet when the phone is silent or on vibrate.
 */
internal class KeySounds(context: Context) {
    private val audioManager: AudioManager? = context.getSystemService(AudioManager::class.java)

    /** [gain] is the volume, from 0 (silent) to 1 (full). */
    fun perform(key: Key, gain: Float) {
        KeySoundEffects.effectFor(key)?.let { play(it, gain) }
    }

    fun suggestionSelected(gain: Float) = play(KeySoundEffects.SUGGESTION_EFFECT, gain)

    private fun play(effect: Int, gain: Float) {
        val manager = audioManager ?: return
        if (gain > 0f && manager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            manager.playSoundEffect(effect, gain)
        }
    }
}
