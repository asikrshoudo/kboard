package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key

/** Turns key presses into edits and tracks the shift state. */
class KeyInputHandler(private val editor: TextEditor) {
    /** True when the next character is typed in its shifted form. */
    var shifted: Boolean = false
        private set

    fun onKey(key: Key) {
        when (key) {
            is Key.Character -> typeCharacter(key)
            is Key.Control -> perform(key.action)
            is Key.Spacer -> Unit
        }
    }

    fun reset() {
        shifted = false
    }

    private fun typeCharacter(key: Key.Character) {
        editor.commitText(if (shifted) key.shifted else key.base)
        shifted = false
    }

    private fun perform(action: ControlAction) {
        when (action) {
            ControlAction.SHIFT -> shifted = !shifted
            ControlAction.BACKSPACE -> editor.deleteBackward()
            ControlAction.ENTER -> editor.performEnter()
            ControlAction.SPACE -> editor.commitText(SPACE)
        }
    }

    private companion object {
        const val SPACE = " "
    }
}
