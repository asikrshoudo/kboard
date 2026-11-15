package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.Layer
import com.kboard.thekami.engine.phonetic.PhoneticComposer
import com.kboard.thekami.engine.suggest.Suggestion
import com.kboard.thekami.engine.suggest.WordContext

/**
 * Turns key presses into edits and tracks shift, the visible page and the
 * language. Finished words go to [learner] only while [learningAllowed], and
 * typos are fixed only while [autocorrectAllowed]. Both are off until the
 * caller has checked the privacy rules for the focused field.
 */
class KeyInputHandler(
    private val editor: TextEditor,
    corrections: CorrectionListener = CorrectionListener.None,
    learner: WordLearner = WordLearner.None,
) {
    /** True when the next character is typed in its shifted form. */
    var shifted: Boolean = false
        private set

    /** The page the keyboard should show. */
    var layer: Layer = Layer.LETTERS
        private set

    var language: Language = Language.ENGLISH_US
        private set

    private val composer = PhoneticComposer(editor)
    private val completer = WordCompleter(editor, composer, learner)
    private val autoCorrect = AutoCorrectFlow(editor, corrections)
    private var autoCapitalize = false

    /** False by default: nothing is learned until the caller has checked the privacy rules. */
    var learningAllowed: Boolean
        get() = completer.allowed
        set(value) {
            completer.allowed = value
        }

    /** False by default. Only English is corrected; Bangla typing never is. */
    var autocorrectAllowed: Boolean
        get() = autoCorrect.enabled
        set(value) {
            autoCorrect.enabled = value
        }

    /** Lowercase words the user undid a correction for. They are never corrected again. */
    var ignoredCorrections: Set<String>
        get() = autoCorrect.ignored
        set(value) {
            autoCorrect.ignored = value
        }

    /** The Latin letters of the Bangla word being typed, empty when there is none. */
    val composingLatin: String get() = composer.latin

    /** Call when a new text field gets focus. */
    fun start(startLayer: Layer, autoCapitalize: Boolean) {
        composer.reset()
        autoCorrect.forget()
        layer = startLayer
        this.autoCapitalize = autoCapitalize
        shifted = false
        refreshAutoShift()
    }

    fun setLanguage(newLanguage: Language) {
        if (newLanguage == language) return
        composer.finish()
        autoCorrect.forget()
        language = newLanguage
        shifted = false
        refreshAutoShift()
    }

    fun onKey(key: Key) {
        // Only an immediate backspace can undo a correction; any other key makes it final.
        val isBackspace = key is Key.Control && key.action == ControlAction.BACKSPACE
        if (!isBackspace) autoCorrect.forget()
        when (key) {
            is Key.Character -> typeCharacter(key)
            is Key.Control -> perform(key.action)
            is Key.Spacer -> Unit
        }
    }

    /** Puts the suggestion in place of the word being typed, followed by a space. */
    fun acceptSuggestion(suggestion: Suggestion) {
        autoCorrect.forget()
        if (composer.isComposing) {
            composer.replaceWith(suggestion.text)
        } else {
            editor.replaceBeforeCursor(suggestion.replaceLength, suggestion.text)
        }
        completer.finishWord()
        editor.commitText(SPACE)
        shifted = false
        refreshAutoShift()
    }

    private fun typeCharacter(key: Key.Character) {
        val text = if (shifted) key.shifted else key.base
        val isLatinLetter = key.base.singleOrNull()?.let { it in 'a'..'z' } == true
        if (language.composes && layer == Layer.LETTERS && isLatinLetter) {
            composer.append(text)
        } else {
            if (text in AUTOCORRECT_PUNCTUATION) correctBefore(text, canUndo = true)
            // Letters continue the word; digits and punctuation end it.
            if (text.all { WordContext.isWordChar(it) }) composer.finish() else completer.finishWord()
            editor.commitText(text)
        }
        shifted = false
    }

    private fun perform(action: ControlAction) {
        when (action) {
            ControlAction.SHIFT -> shifted = !shifted
            ControlAction.BACKSPACE -> backspace()
            ControlAction.ENTER -> {
                correctBefore(NEWLINE, canUndo = false)
                completer.finishWord()
                editor.performEnter()
                refreshAutoShift()
            }
            ControlAction.SPACE -> {
                correctBefore(SPACE, canUndo = true)
                completer.finishWord()
                editor.commitText(SPACE)
                refreshAutoShift()
            }
            ControlAction.SHOW_LETTERS -> showLayer(Layer.LETTERS)
            ControlAction.SHOW_SYMBOLS -> showLayer(Layer.SYMBOLS)
            ControlAction.SHOW_MORE_SYMBOLS -> showLayer(Layer.MORE_SYMBOLS)
            ControlAction.SWITCH_LANGUAGE -> setLanguage(language.next())
        }
    }

    /** Undoes a correction first, then edits the Bangla word, and only then deletes text. */
    private fun backspace() {
        when {
            autoCorrect.undo() -> refreshAutoShift()
            composer.backspace() -> Unit
            else -> {
                editor.deleteBackward()
                refreshAutoShift()
            }
        }
    }

    private fun showLayer(newLayer: Layer) {
        composer.finish()
        layer = newLayer
    }

    private fun correctBefore(delimiter: String, canUndo: Boolean) {
        if (!language.composes) autoCorrect.correctWordBeforeCursor(delimiter, canUndo)
    }

    /** Asks the field for context only after keys that can end a sentence. English only. */
    private fun refreshAutoShift() {
        if (!autoCapitalize || language.composes) return
        val before = editor.textBeforeCursor(LOOKBEHIND_CHARS) ?: return
        shifted = SentenceCase.startsSentence(before)
    }

    private companion object {
        const val SPACE = " "
        const val NEWLINE = "\n"

        /** How much text before the cursor is enough to find a sentence end. */
        const val LOOKBEHIND_CHARS = 32

        /** Typing one of these also ends the word, so the word before it is checked for typos. */
        val AUTOCORRECT_PUNCTUATION = setOf(",", ".", "!", "?", ";", ":")
    }
}
