package com.kboard.thekami.data.settings

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

/** One DataStore per process. The app and the keyboard service share this process. */
private val Context.settingsDataStore by preferencesDataStore(name = "kboard_settings")

fun createSettingsRepository(context: Context): SettingsRepository =
    SettingsRepository(context.applicationContext.settingsDataStore)
