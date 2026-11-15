package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyInputHandlerTest {
    private val editor = FakeTextEditor()
    private val handler = KeyInputHandler(editor)
    private val shift = controlKey(ControlAction.SHIFT)
    private val space = controlKey(ControlAction.SPACE)

    private fun type(text: String) = text.forEach { handler.onKey(letterKey(it)) }

    @Test
    fun characterTypesItsBaseText() {
        type("a")
        assertEquals("a", editor.content)
    }

    @Test
    fun shiftAppliesToTheNextCharacterOnly() {
        handler.onKey(shift)
        type("aa")
        assertEquals("Aa", editor.content)
        assertFalse(handler.shifted)
    }

    @Test
    fun shiftPressedTwiceCancels() {
        handler.onKey(shift)
        assertTrue(handler.shifted)
        handler.onKey(shift)
        assertFalse(handler.shifted)
    }

    @Test
    fun startClearsShiftWhenTheFieldDoesNotAskForCapitals() {
        handler.onKey(shift)
        handler.start(Layer.LETTERS, autoCapitalize = false)
        assertFalse(handler.shifted)
    }

    @Test
    fun controlKeysEditTheField() {
        type("ab")
        handler.onKey(space)
        handler.onKey(controlKey(ControlAction.BACKSPACE))
        assertEquals("ab", editor.content)
        handler.onKey(controlKey(ControlAction.ENTER))
        assertEquals("enter", editor.events.last())
    }

    @Test
    fun spacerDoesNothing() {
        handler.onKey(Key.Spacer(1f))
        assertTrue(editor.events.isEmpty())
    }

    @Test
    fun layerKeysSwitchThePage() {
        handler.onKey(controlKey(ControlAction.SHOW_SYMBOLS))
        assertEquals(Layer.SYMBOLS, handler.layer)
        handler.onKey(controlKey(ControlAction.SHOW_MORE_SYMBOLS))
        assertEquals(Layer.MORE_SYMBOLS, handler.layer)
        handler.onKey(controlKey(ControlAction.SHOW_LETTERS))
        assertEquals(Layer.LETTERS, handler.layer)
    }

    @Test
    fun startOpensTheRequestedPage() {
        handler.start(Layer.SYMBOLS, autoCapitalize = false)
        assertEquals(Layer.SYMBOLS, handler.layer)
    }

    @Test
    fun emptyFieldStartsShiftedWhenCapitalsAreRequested() {
        handler.start(Layer.LETTERS, autoCapitalize = true)
        assertTrue(handler.shifted)
    }

    @Test
    fun fieldWithoutCapitalsRequestStaysUnshifted() {
        handler.start(Layer.LETTERS, autoCapitalize = false)
        assertFalse(handler.shifted)
    }

    @Test
    fun spaceAfterASentenceEndCapitalizesTheNextLetter() {
        editor.setText("Hello")
        handler.start(Layer.LETTERS, autoCapitalize = true)
        assertFalse(handler.shifted)
        editor.setText("Hello.")
        handler.onKey(space)
        assertEquals("Hello. ", editor.content)
        assertTrue(handler.shifted)
    }

    @Test
    fun unavailableContextLeavesShiftAlone() {
        handler.start(Layer.LETTERS, autoCapitalize = true)
        editor.contextAvailable = false
        handler.onKey(space)
        assertTrue(handler.shifted)
    }
}
