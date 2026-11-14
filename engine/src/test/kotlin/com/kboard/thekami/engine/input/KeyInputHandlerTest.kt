package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyInputHandlerTest {
    private val editor = RecordingEditor()
    private val handler = KeyInputHandler(editor)
    private val letterA = Key.Character(base = "a", shifted = "A")
    private val shift = Key.Control(ControlAction.SHIFT, 1f)

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
    fun resetClearsShift() {
        handler.onKey(shift)
        handler.reset()
        assertFalse(handler.shifted)
    }

    @Test
    fun controlKeysCallTheEditor() {
        handler.onKey(Key.Control(ControlAction.SPACE, 1f))
        handler.onKey(Key.Control(ControlAction.BACKSPACE, 1f))
        handler.onKey(Key.Control(ControlAction.ENTER, 1f))
        assertEquals(listOf("commit: ", "delete", "enter"), editor.events)
    }

    @Test
    fun spacerDoesNothing() {
        handler.onKey(Key.Spacer(1f))
        assertTrue(editor.events.isEmpty())
    }

    private class RecordingEditor : TextEditor {
        val events = mutableListOf<String>()

        override fun commitText(text: String) {
            events += "commit:$text"
        }

        override fun deleteBackward() {
            events += "delete"
        }

        override fun performEnter() {
            events += "enter"
        }
    }
}
