package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.Layer
import com.kboard.thekami.engine.phonetic.PhoneticComposer

/** Turns key presses into edits and tracks shift, the visible page and the language. */
class KeyInputHandler(private val editor: TextEditor) {
    /** True when the next character is typed in its shifted form. */
    var shifted: Boolean = false
        private set

    /** The page the keyboard should show. */
    var layer: Layer = Layer.LETTERS
        private set

    var language: Language = Language.ENGLISH_US
        private set

    private val composer = PhoneticComposer(editor)
    private var autoCapitalize = false

    /** Call when a new text field gets focus. */
    fun start(startLayer: Layer, autoCapitalize: Boolean) {
        composer.reset()
        layer = startLayer
        this.autoCapitalize = autoCapitalize
        shifted = false
        refreshAutoShift()
    }

    fun setLanguage(newLanguage: Language) {
        if (newLanguage == language) return
        composer.finish()
        language = newLanguage
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
        val text = if (shifted) key.shifted else key.base
        if (isComposingLetter(key)) {
            composer.append(text)
        } else {
            composer.finish()
            editor.commitText(text)
        }
        shifted = false
    }

    /** In a composing language, Latin letters on the letters page build a word. */
    private fun isComposingLetter(key: Key.Character): Boolean =
        language.composes && layer == Layer.LETTERS && key.base.singleOrNull()?.let { it in 'a'..'z' } == true

    private fun perform(action: ControlAction) {
        when (action) {
            ControlAction.SHIFT -> shifted = !shifted
            ControlAction.BACKSPACE -> backspace()
            ControlAction.ENTER -> {
                composer.finish()
                editor.performEnter()
                refreshAutoShift()
            }
            ControlAction.SPACE -> {
                composer.finish()
                editor.commitText(SPACE)
                refreshAutoShift()
            }
            ControlAction.SHOW_LETTERS -> showLayer(Layer.LETTERS)
            ControlAction.SHOW_SYMBOLS -> showLayer(Layer.SYMBOLS)
            ControlAction.SHOW_MORE_SYMBOLS -> showLayer(Layer.MORE_SYMBOLS)
            ControlAction.SWITCH_LANGUAGE -> setLanguage(language.next())
        }
    }

    private fun backspace() {
        if (composer.backspace()) return
        editor.deleteBackward()
        refreshAutoShift()
    }

    private fun showLayer(newLayer: Layer) {
        composer.finish()
        layer = newLayer
    }

    /** Asks the field for context only after keys that can end a sentence. English only. */
    private fun refreshAutoShift() {
        if (!autoCapitalize || language.composes) return
        val before = editor.textBeforeCursor(LOOKBEHIND_CHARS) ?: return
        shifted = SentenceCase.startsSentence(before)
    }

    private companion object {
        const val SPACE = " "

        /** How much text before the cursor is enough to find a sentence end. */
        const val LOOKBEHIND_CHARS = 32
    }
}
