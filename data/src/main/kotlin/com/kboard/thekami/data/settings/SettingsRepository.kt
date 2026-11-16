package com.kboard.thekami.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Reads and writes [KeyboardSettings] on the device. Nothing leaves the phone. */
class SettingsRepository internal constructor(private val store: DataStore<Preferences>) {
    val settings: Flow<KeyboardSettings> = store.data
        .catch { error ->
            // An unreadable file falls back to defaults instead of crashing the keyboard.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            KeyboardSettings(
                hapticsEnabled = preferences[HAPTICS_ENABLED] ?: DEFAULTS.hapticsEnabled,
                languageId = preferences[LANGUAGE_ID] ?: DEFAULTS.languageId,
                incognitoEnabled = preferences[INCOGNITO_ENABLED] ?: DEFAULTS.incognitoEnabled,
                autocorrectEnabled = preferences[AUTOCORRECT_ENABLED] ?: DEFAULTS.autocorrectEnabled,
                ignoredCorrections = preferences[IGNORED_CORRECTIONS] ?: DEFAULTS.ignoredCorrections,
                appearance = appearanceOf(preferences),
            )
        }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        store.edit { it[HAPTICS_ENABLED] = enabled }
    }

    suspend fun setLanguageId(id: String) {
        store.edit { it[LANGUAGE_ID] = id }
    }

    suspend fun setIncognitoEnabled(enabled: Boolean) {
        store.edit { it[INCOGNITO_ENABLED] = enabled }
    }

    suspend fun setAutocorrectEnabled(enabled: Boolean) {
        store.edit { it[AUTOCORRECT_ENABLED] = enabled }
    }

    /** Remembers that the user wants [word] left alone. */
    suspend fun addIgnoredCorrection(word: String) {
        store.edit { it[IGNORED_CORRECTIONS] = (it[IGNORED_CORRECTIONS] ?: emptySet()) + word }
    }

    suspend fun clearIgnoredCorrections() {
        store.edit { it.remove(IGNORED_CORRECTIONS) }
    }

    suspend fun setNumberRowEnabled(enabled: Boolean) {
        store.edit { it[NUMBER_ROW_ENABLED] = enabled }
    }

    /** Saved values outside the allowed range are moved to the nearest allowed value. */
    suspend fun setKeyboardHeightPercent(percent: Int) {
        store.edit { it[HEIGHT_PERCENT] = clampHeight(percent) }
    }

    suspend fun setKeyPreviewEnabled(enabled: Boolean) {
        store.edit { it[KEY_PREVIEW_ENABLED] = enabled }
    }

    private fun appearanceOf(preferences: Preferences): KeyboardAppearance {
        val defaults = DEFAULTS.appearance
        return KeyboardAppearance(
            numberRowEnabled = preferences[NUMBER_ROW_ENABLED] ?: defaults.numberRowEnabled,
            heightPercent = clampHeight(preferences[HEIGHT_PERCENT] ?: defaults.heightPercent),
            keyPreviewEnabled = preferences[KEY_PREVIEW_ENABLED] ?: defaults.keyPreviewEnabled,
        )
    }

    private fun clampHeight(percent: Int): Int =
        percent.coerceIn(KeyboardAppearance.MIN_HEIGHT_PERCENT, KeyboardAppearance.MAX_HEIGHT_PERCENT)

    private companion object {
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val LANGUAGE_ID = stringPreferencesKey("language_id")
        val INCOGNITO_ENABLED = booleanPreferencesKey("incognito_enabled")
        val AUTOCORRECT_ENABLED = booleanPreferencesKey("autocorrect_enabled")
        val IGNORED_CORRECTIONS = stringSetPreferencesKey("ignored_corrections")
        val NUMBER_ROW_ENABLED = booleanPreferencesKey("number_row_enabled")
        val HEIGHT_PERCENT = intPreferencesKey("keyboard_height_percent")
        val KEY_PREVIEW_ENABLED = booleanPreferencesKey("key_preview_enabled")
        val DEFAULTS = KeyboardSettings()
    }
}
