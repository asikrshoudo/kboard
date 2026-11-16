package com.kboard.thekami.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

/** Saves the key sound choices. */
class SoundEditor internal constructor(private val store: DataStore<Preferences>) {
    suspend fun setEnabled(enabled: Boolean) {
        store.edit { it[PreferenceKeys.SOUND_ENABLED] = enabled }
    }

    /** A volume outside 0 to 100 is moved to the nearest allowed value. */
    suspend fun setVolumePercent(percent: Int) {
        store.edit { it[PreferenceKeys.SOUND_VOLUME_PERCENT] = clampVolume(percent) }
    }

    internal companion object {
        fun read(preferences: Preferences): KeyboardSound {
            val defaults = KeyboardSound()
            return KeyboardSound(
                enabled = preferences[PreferenceKeys.SOUND_ENABLED] ?: defaults.enabled,
                volumePercent = clampVolume(preferences[PreferenceKeys.SOUND_VOLUME_PERCENT] ?: defaults.volumePercent),
            )
        }

        private fun clampVolume(percent: Int): Int =
            percent.coerceIn(KeyboardSound.MIN_VOLUME_PERCENT, KeyboardSound.MAX_VOLUME_PERCENT)
    }
}
