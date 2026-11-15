package com.kboard.thekami.data.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.repository() = SettingsRepository(
        PreferenceDataStoreFactory.create(scope = backgroundScope) {
            File(folder.root, "settings.preferences_pb")
        },
    )

    @Test
    fun hapticsAreOnByDefault() = runTest {
        assertTrue(repository().settings.first().hapticsEnabled)
    }

    @Test
    fun savedHapticsChoiceIsReadBack() = runTest {
        val repository = repository()
        repository.setHapticsEnabled(false)
        assertFalse(repository.settings.first().hapticsEnabled)
        repository.setHapticsEnabled(true)
        assertTrue(repository.settings.first().hapticsEnabled)
    }

    @Test
    fun languageIsEmptyByDefaultAndSavedChoiceIsReadBack() = runTest {
        val repository = repository()
        assertEquals("", repository.settings.first().languageId)
        repository.setLanguageId("bn-BD-phonetic")
        assertEquals("bn-BD-phonetic", repository.settings.first().languageId)
    }

    @Test
    fun incognitoIsOffByDefaultAndSavedChoiceIsReadBack() = runTest {
        val repository = repository()
        assertFalse(repository.settings.first().incognitoEnabled)
        repository.setIncognitoEnabled(true)
        assertTrue(repository.settings.first().incognitoEnabled)
    }
}
