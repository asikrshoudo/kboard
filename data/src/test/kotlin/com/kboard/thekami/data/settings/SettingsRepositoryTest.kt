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

    @Test
    fun autocorrectIsOnByDefaultAndSavedChoiceIsReadBack() = runTest {
        val repository = repository()
        assertTrue(repository.settings.first().autocorrectEnabled)
        repository.setAutocorrectEnabled(false)
        assertFalse(repository.settings.first().autocorrectEnabled)
    }

    @Test
    fun ignoredCorrectionsAreCollectedWithoutDuplicatesAndCanBeCleared() = runTest {
        val repository = repository()
        assertTrue(repository.settings.first().ignoredCorrections.isEmpty())
        repository.addIgnoredCorrection("teh")
        repository.addIgnoredCorrection("teh")
        repository.addIgnoredCorrection("dont")
        assertEquals(setOf("teh", "dont"), repository.settings.first().ignoredCorrections)
        repository.clearIgnoredCorrections()
        assertTrue(repository.settings.first().ignoredCorrections.isEmpty())
    }

    @Test
    fun appearanceHasTheDocumentedDefaults() = runTest {
        val appearance = repository().settings.first().appearance
        assertFalse(appearance.numberRowEnabled)
        assertEquals(KeyboardAppearance.DEFAULT_HEIGHT_PERCENT, appearance.heightPercent)
        assertTrue(appearance.keyPreviewEnabled)
    }

    @Test
    fun numberRowAndKeyPreviewChoicesAreReadBack() = runTest {
        val repository = repository()
        repository.setNumberRowEnabled(true)
        repository.setKeyPreviewEnabled(false)
        val appearance = repository.settings.first().appearance
        assertTrue(appearance.numberRowEnabled)
        assertFalse(appearance.keyPreviewEnabled)
    }

    @Test
    fun heightIsSavedAndKeptInsideTheAllowedRange() = runTest {
        val repository = repository()
        repository.setKeyboardHeightPercent(110)
        assertEquals(110, repository.settings.first().appearance.heightPercent)
        repository.setKeyboardHeightPercent(KeyboardAppearance.MAX_HEIGHT_PERCENT + 100)
        assertEquals(KeyboardAppearance.MAX_HEIGHT_PERCENT, repository.settings.first().appearance.heightPercent)
        repository.setKeyboardHeightPercent(0)
        assertEquals(KeyboardAppearance.MIN_HEIGHT_PERCENT, repository.settings.first().appearance.heightPercent)
    }
}
