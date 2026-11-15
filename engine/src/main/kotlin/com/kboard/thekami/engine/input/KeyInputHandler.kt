package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.Layer

/** Turns key presses into edits and tracks the shift state and the visible page. */
class KeyInputHandler(private val editor: TextEditor) {
    /** True when the next character is typed in its shifted form. */
    var shifted: Boolean = false
        private set

    /** The page the keyboard should show. */
    var layer: Layer = Layer.LETTERS
        private set

    private var autoCapitalize = false

    /** Call when a new text field gets focus. */
    fun start(startLayer: Layer, autoCapitalize: Boolean) {
        layer = startLayer
        this.autoCapitalize = autoCapitalize
        shifted = false
        refreshAutoShift()
    }

    fun onKey(key: Key) {
        when (key) {
            is Key.Character -> typeCharacter(key)
            is Key.Control -> perform(key.action)
            is Key.Spacer -> Unit
        }
    }

    private fun typeCharacter(key: Key.Character) {
        editor.commitText(if (shifted) key.shifted else key.base)
        // A typed character is never whitespace, so it cannot start a sentence.
        shifted = false
    }

    private fun perform(action: ControlAction) {
        when (action) {
            ControlAction.SHIFT -> shifted = !shifted
            ControlAction.BACKSPACE -> {
                editor.deleteBackward()
                refreshAutoShift()
            }
            ControlAction.ENTER -> {
                editor.performEnter()
                refreshAutoShift()
            }
            ControlAction.SPACE -> {
                editor.commitText(SPACE)
                refreshAutoShift()
            }
            ControlAction.SHOW_LETTERS -> layer = Layer.LETTERS
            ControlAction.SHOW_SYMBOLS -> layer = Layer.SYMBOLS
            ControlAction.SHOW_MORE_SYMBOLS -> layer = Layer.MORE_SYMBOLS
        }
    }

    /** Asks the field for context only after keys that can end a sentence. */
    private fun refreshAutoShift() {
        if (!autoCapitalize) return
        val before = editor.textBeforeCursor(LOOKBEHIND_CHARS) ?: return
        shifted = SentenceCase.startsSentence(before)
    }

    private companion object {
        const val SPACE = " "

        /** How much text before the cursor is enough to find a sentence end. */
        const val LOOKBEHIND_CHARS = 32
    }
}
