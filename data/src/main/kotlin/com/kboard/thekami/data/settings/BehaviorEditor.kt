package com.kboard.thekami.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

/** Saves the choices about gestures. */
class BehaviorEditor internal constructor(private val store: DataStore<Preferences>) {
    suspend fun setSpaceLongPress(action: SpaceLongPress) {
        store.edit { it[PreferenceKeys.SPACE_LONG_PRESS] = action.id }
    }

    internal companion object {
        fun read(preferences: Preferences): KeyboardBehavior =
            KeyboardBehavior(spaceLongPress = SpaceLongPress.fromId(preferences[PreferenceKeys.SPACE_LONG_PRESS]))
    }
}
