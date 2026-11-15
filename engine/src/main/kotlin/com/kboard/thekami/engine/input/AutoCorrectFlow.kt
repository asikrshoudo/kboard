package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.autocorrect.AutoCorrector
import com.kboard.thekami.engine.suggest.WordContext

/**
 * Fixes the word before the cursor when it is a known typo, and lets the user
 * undo the most recent fix. Does nothing while [enabled] is false.
 */
internal class AutoCorrectFlow(
    private val editor: TextEditor,
    private val listener: CorrectionListener,
) {
    var enabled = false

    /** Words the user undid a correction for. */
    var ignored: Set<String> = emptySet()

    private val corrector = AutoCorrector { ignored }
    private var last: Applied? = null

    private class Applied(val original: String, val corrected: String, val delimiter: String)

    /** Call just before typing [delimiter]. [canUndo] is false when the delimiter cannot be taken back. */
    fun correctWordBeforeCursor(delimiter: String, canUndo: Boolean) {
        last = null
        val word = wordBeforeCursor() ?: return
        val corrected = corrector.correctionFor(word, delimiter) ?: return
        editor.replaceBeforeCursor(word.length, corrected)
        if (canUndo) last = Applied(word, corrected, delimiter)
    }

    /**
     * Puts the typed word back if the last thing the user did was an automatic
     * correction. Returns true when it did, so the key press is used up.
     */
    fun undo(): Boolean {
        val applied = last.also { last = null } ?: return false
        val fixedText = applied.corrected + applied.delimiter
        val intact = editor.textBeforeCursor(fixedText.length) == fixedText
        if (intact) {
            editor.replaceBeforeCursor(fixedText.length, applied.original)
            val word = applied.original.lowercase()
            ignored = ignored + word
            listener.onCorrectionRejected(word)
        }
        return intact
    }

    /** Any other key press means the correction can no longer be undone. */
    fun forget() {
        last = null
    }

    private fun wordBeforeCursor(): String? =
        if (enabled) editor.textBeforeCursor(LOOKBEHIND_CHARS)?.let { WordContext.parse(it).current } else null

    private companion object {
        /** Long enough for any single word. */
        const val LOOKBEHIND_CHARS = 32
    }
}
