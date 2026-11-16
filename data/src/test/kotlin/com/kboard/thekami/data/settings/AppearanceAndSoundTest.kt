package com.kboard.thekami.data.settings

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AppearanceAndSoundTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.repository() = settingsRepository(folder.root)

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
        repository.appearance.setNumberRowEnabled(true)
        repository.appearance.setKeyPreviewEnabled(false)
        val appearance = repository.settings.first().appearance
        assertTrue(appearance.numberRowEnabled)
        assertFalse(appearance.keyPreviewEnabled)
    }

    @Test
    fun heightIsSavedAndKeptInsideTheAllowedRange() = runTest {
        val repository = repository()
        repository.appearance.setHeightPercent(110)
        assertEquals(110, repository.settings.first().appearance.heightPercent)
        repository.appearance.setHeightPercent(KeyboardAppearance.MAX_HEIGHT_PERCENT + 100)
        assertEquals(KeyboardAppearance.MAX_HEIGHT_PERCENT, repository.settings.first().appearance.heightPercent)
        repository.appearance.setHeightPercent(0)
        assertEquals(KeyboardAppearance.MIN_HEIGHT_PERCENT, repository.settings.first().appearance.heightPercent)
    }

    @Test
    fun soundIsOffByDefaultAtTheDefaultVolume() = runTest {
        val sound = repository().settings.first().sound
        assertFalse(sound.enabled)
        assertEquals(KeyboardSound.DEFAULT_VOLUME_PERCENT, sound.volumePercent)
    }

    @Test
    fun soundChoicesAreReadBack() = runTest {
        val repository = repository()
        repository.sound.setEnabled(true)
        repository.sound.setVolumePercent(70)
        val sound = repository.settings.first().sound
        assertTrue(sound.enabled)
        assertEquals(70, sound.volumePercent)
    }

    @Test
    fun volumeIsKeptInsideZeroToOneHundred() = runTest {
        val repository = repository()
        repository.sound.setVolumePercent(KeyboardSound.MAX_VOLUME_PERCENT + 50)
        assertEquals(KeyboardSound.MAX_VOLUME_PERCENT, repository.settings.first().sound.volumePercent)
        repository.sound.setVolumePercent(-5)
        assertEquals(KeyboardSound.MIN_VOLUME_PERCENT, repository.settings.first().sound.volumePercent)
    }

    @Test
    fun gainIsSilentAtZeroFullAtOneHundredAndCurvedBetween() {
        assertEquals(0f, KeyboardSound(volumePercent = 0).gain, TOLERANCE)
        assertEquals(1f, KeyboardSound(volumePercent = 100).gain, TOLERANCE)
        assertEquals(QUARTER, KeyboardSound(volumePercent = 50).gain, TOLERANCE)
    }

    @Test
    fun aLouderSettingNeverSoundsQuieter() {
        val gains = (0..100 step KeyboardSound.VOLUME_STEP_PERCENT).map { KeyboardSound(volumePercent = it).gain }
        assertEquals(gains.sorted(), gains)
    }

    private companion object {
        const val TOLERANCE = 0.0001f
        const val QUARTER = 0.25f
    }
}
