package com.kboard.thekami.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Reads and writes [KeyboardSettings] on the device. Nothing leaves the phone.
 * The layout and sound choices are saved through [appearance] and [sound].
 */
class SettingsRepository internal constructor(private val store: DataStore<Preferences>) {
    /** Saves how the keyboard looks and is laid out. */
    val appearance = AppearanceEditor(store)

    /** Saves the key sound choices. */
    val sound = SoundEditor(store)

    val settings: Flow<KeyboardSettings> = store.data
        .catch { error ->
            // An unreadable file falls back to defaults instead of crashing the keyboard.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            KeyboardSettings(
                hapticsEnabled = preferences[PreferenceKeys.HAPTICS_ENABLED] ?: DEFAULTS.hapticsEnabled,
                languageId = preferences[PreferenceKeys.LANGUAGE_ID] ?: DEFAULTS.languageId,
                incognitoEnabled = preferences[PreferenceKeys.INCOGNITO_ENABLED] ?: DEFAULTS.incognitoEnabled,
                autocorrectEnabled = preferences[PreferenceKeys.AUTOCORRECT_ENABLED] ?: DEFAULTS.autocorrectEnabled,
                ignoredCorrections = preferences[PreferenceKeys.IGNORED_CORRECTIONS] ?: DEFAULTS.ignoredCorrections,
                appearance = AppearanceEditor.read(preferences),
                sound = SoundEditor.read(preferences),
            )
        }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        store.edit { it[PreferenceKeys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setLanguageId(id: String) {
        store.edit { it[PreferenceKeys.LANGUAGE_ID] = id }
    }

    suspend fun setIncognitoEnabled(enabled: Boolean) {
        store.edit { it[PreferenceKeys.INCOGNITO_ENABLED] = enabled }
    }

    suspend fun setAutocorrectEnabled(enabled: Boolean) {
        store.edit { it[PreferenceKeys.AUTOCORRECT_ENABLED] = enabled }
    }

    /** Remembers that the user wants [word] left alone. */
    suspend fun addIgnoredCorrection(word: String) {
        store.edit {
            it[PreferenceKeys.IGNORED_CORRECTIONS] = (it[PreferenceKeys.IGNORED_CORRECTIONS] ?: emptySet()) + word
        }
    }

    suspend fun clearIgnoredCorrections() {
        store.edit { it.remove(PreferenceKeys.IGNORED_CORRECTIONS) }
    }

    private companion object {
        val DEFAULTS = KeyboardSettings()
    }
}
