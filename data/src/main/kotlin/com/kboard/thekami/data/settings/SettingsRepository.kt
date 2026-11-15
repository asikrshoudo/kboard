package com.kboard.thekami.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
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
            )
        }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        store.edit { it[HAPTICS_ENABLED] = enabled }
    }

    suspend fun setLanguageId(id: String) {
        store.edit { it[LANGUAGE_ID] = id }
    }

    private companion object {
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val LANGUAGE_ID = stringPreferencesKey("language_id")
        val DEFAULTS = KeyboardSettings()
    }
}
