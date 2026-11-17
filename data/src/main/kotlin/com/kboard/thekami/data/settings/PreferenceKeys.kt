package com.kboard.thekami.data.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

/** The names under which settings are saved. Changing a name makes users lose that setting. */
internal object PreferenceKeys {
    val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
    val LANGUAGE_ID = stringPreferencesKey("language_id")
    val INCOGNITO_ENABLED = booleanPreferencesKey("incognito_enabled")
    val AUTOCORRECT_ENABLED = booleanPreferencesKey("autocorrect_enabled")
    val IGNORED_CORRECTIONS = stringSetPreferencesKey("ignored_corrections")
    val NUMBER_ROW_ENABLED = booleanPreferencesKey("number_row_enabled")
    val HEIGHT_PERCENT = intPreferencesKey("keyboard_height_percent")
    val KEY_PREVIEW_ENABLED = booleanPreferencesKey("key_preview_enabled")
    val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    val SOUND_VOLUME_PERCENT = intPreferencesKey("sound_volume_percent")
    val SPACE_LONG_PRESS = stringPreferencesKey("space_long_press")
}
