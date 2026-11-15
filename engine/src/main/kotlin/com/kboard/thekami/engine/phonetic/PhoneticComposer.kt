package com.kboard.thekami.engine.phonetic

import com.kboard.thekami.engine.input.TextEditor

/**
 * Collects typed letters and shows them as Bangla in the text field's
 * composing region. Nothing is final until [finish] is called.
 */
class PhoneticComposer(private val editor: TextEditor) {
    private val letters = StringBuilder()
    private var shown = ""

    fun append(letter: String) {
        dropIfCursorMoved()
        letters.append(letter)
        render()
    }

    /** Removes the last typed letter. Returns false when no word is being composed. */
    fun backspace(): Boolean {
        dropIfCursorMoved()
        if (letters.isEmpty()) return false
        letters.deleteAt(letters.lastIndex)
        render()
        return true
    }

    /** Keeps what is shown as ordinary text and starts a new word next time. */
    fun finish() {
        if (letters.isEmpty()) return
        editor.finishComposing()
        clear()
    }

    /** Forgets the letters without touching the field, for when another field has focus. */
    fun reset() = clear()

    private fun render() {
        shown = PhoneticTransliterator.transliterate(letters.toString())
        editor.setComposingText(shown)
    }

    /** If the text before the cursor is no longer our word, the user moved the cursor. */
    private fun dropIfCursorMoved() {
        if (letters.isEmpty()) return
        val before = editor.textBeforeCursor(shown.length)
        if (before != null && before != shown) finish()
    }

    private fun clear() {
        letters.clear()
        shown = ""
    }
}
