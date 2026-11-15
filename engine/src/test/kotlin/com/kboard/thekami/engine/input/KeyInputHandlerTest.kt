package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyInputHandlerTest {
    private val editor = RecordingEditor()
    private val handler = KeyInputHandler(editor)
    private val letterA = Key.Character(base = "a", shifted = "A")
    private val shift = Key.Control(ControlAction.SHIFT, 1f)
    private val space = Key.Control(ControlAction.SPACE, 1f)

    @Test
    fun characterTypesItsBaseText() {
        handler.onKey(letterA)
        assertEquals(listOf("commit:a"), editor.events)
    }

    @Test
    fun shiftAppliesToTheNextCharacterOnly() {
        handler.onKey(shift)
        handler.onKey(letterA)
        handler.onKey(letterA)
        assertEquals(listOf("commit:A", "commit:a"), editor.events)
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
    fun controlKeysCallTheEditor() {
        handler.onKey(space)
        handler.onKey(Key.Control(ControlAction.BACKSPACE, 1f))
        handler.onKey(Key.Control(ControlAction.ENTER, 1f))
        assertEquals(listOf("commit: ", "delete", "enter"), editor.events)
    }

    @Test
    fun spacerDoesNothing() {
        handler.onKey(Key.Spacer(1f))
        assertTrue(editor.events.isEmpty())
    }

    @Test
    fun layerKeysSwitchThePage() {
        handler.onKey(Key.Control(ControlAction.SHOW_SYMBOLS, 1f))
        assertEquals(Layer.SYMBOLS, handler.layer)
        handler.onKey(Key.Control(ControlAction.SHOW_MORE_SYMBOLS, 1f))
        assertEquals(Layer.MORE_SYMBOLS, handler.layer)
        handler.onKey(Key.Control(ControlAction.SHOW_LETTERS, 1f))
        assertEquals(Layer.LETTERS, handler.layer)
    }

    @Test
    fun startOpensTheRequestedPage() {
        handler.start(Layer.SYMBOLS, autoCapitalize = false)
        assertEquals(Layer.SYMBOLS, handler.layer)
    }

    @Test
    fun emptyFieldStartsShiftedWhenCapitalsAreRequested() {
        editor.textBefore = ""
        handler.start(Layer.LETTERS, autoCapitalize = true)
        assertTrue(handler.shifted)
    }

    @Test
    fun fieldWithoutCapitalsRequestStaysUnshifted() {
        editor.textBefore = ""
        handler.start(Layer.LETTERS, autoCapitalize = false)
        assertFalse(handler.shifted)
    }

    @Test
    fun spaceAfterASentenceEndCapitalizesTheNextLetter() {
        editor.textBefore = "Hello"
        handler.start(Layer.LETTERS, autoCapitalize = true)
        assertFalse(handler.shifted)
        editor.textBefore = "Hello. "
        handler.onKey(space)
        assertTrue(handler.shifted)
    }

    @Test
    fun unavailableContextLeavesShiftAlone() {
        editor.textBefore = ""
        handler.start(Layer.LETTERS, autoCapitalize = true)
        editor.textBefore = null
        handler.onKey(space)
        assertTrue(handler.shifted)
    }

    private class RecordingEditor : TextEditor {
        val events = mutableListOf<String>()
        var textBefore: String? = ""

        override fun commitText(text: String) {
            events += "commit:$text"
        }

        override fun deleteBackward() {
            events += "delete"
        }

        override fun performEnter() {
            events += "enter"
        }

        override fun textBeforeCursor(length: Int): String? = textBefore
    }
}
