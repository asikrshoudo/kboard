package com.kboard.thekami.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

/** Saves the choices about how the keyboard looks and is laid out. */
class AppearanceEditor internal constructor(private val store: DataStore<Preferences>) {
    suspend fun setNumberRowEnabled(enabled: Boolean) {
        store.edit { it[PreferenceKeys.NUMBER_ROW_ENABLED] = enabled }
    }

    /** A height outside the allowed range is moved to the nearest allowed value. */
    suspend fun setHeightPercent(percent: Int) {
        store.edit { it[PreferenceKeys.HEIGHT_PERCENT] = clampHeight(percent) }
    }

    suspend fun setKeyPreviewEnabled(enabled: Boolean) {
        store.edit { it[PreferenceKeys.KEY_PREVIEW_ENABLED] = enabled }
    }

    internal companion object {
        fun read(preferences: Preferences): KeyboardAppearance {
            val defaults = KeyboardAppearance()
            return KeyboardAppearance(
                numberRowEnabled = preferences[PreferenceKeys.NUMBER_ROW_ENABLED] ?: defaults.numberRowEnabled,
                heightPercent = clampHeight(preferences[PreferenceKeys.HEIGHT_PERCENT] ?: defaults.heightPercent),
                keyPreviewEnabled = preferences[PreferenceKeys.KEY_PREVIEW_ENABLED] ?: defaults.keyPreviewEnabled,
            )
        }

        private fun clampHeight(percent: Int): Int =
            percent.coerceIn(KeyboardAppearance.MIN_HEIGHT_PERCENT, KeyboardAppearance.MAX_HEIGHT_PERCENT)
    }
}
