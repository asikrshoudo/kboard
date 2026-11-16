package com.kboard.thekami.data.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.test.TestScope

/** A repository that saves into [folder], for tests. */
internal fun TestScope.settingsRepository(folder: File) = SettingsRepository(
    PreferenceDataStoreFactory.create(scope = backgroundScope) {
        File(folder, "settings.preferences_pb")
    },
)
