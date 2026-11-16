package com.kboard.thekami.ime.feedback

import android.media.AudioManager
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class KeySoundEffectsTest {
    private fun control(action: ControlAction) = Key.Control(action, 1f)

    @Test
    fun lettersUseTheStandardClick() {
        assertEquals(AudioManager.FX_KEYPRESS_STANDARD, KeySoundEffects.effectFor(Key.Character("a", "A")))
    }

    @Test
    fun spaceDeleteAndEnterEachHaveTheirOwnSound() {
        assertEquals(AudioManager.FX_KEYPRESS_SPACEBAR, KeySoundEffects.effectFor(control(ControlAction.SPACE)))
        assertEquals(AudioManager.FX_KEYPRESS_DELETE, KeySoundEffects.effectFor(control(ControlAction.BACKSPACE)))
        assertEquals(AudioManager.FX_KEYPRESS_RETURN, KeySoundEffects.effectFor(control(ControlAction.ENTER)))
    }

    @Test
    fun everyControlKeyMakesASound() {
        ControlAction.entries.forEach { assertNotNull(it.name, KeySoundEffects.effectFor(control(it))) }
    }

    @Test
    fun aSpacerIsSilent() {
        assertNull(KeySoundEffects.effectFor(Key.Spacer(1f)))
    }
}
