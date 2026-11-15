package com.kboard.thekami.engine.input

/** Decides when the next letter should be capitalized. */
object SentenceCase {
    private val TERMINATORS = setOf('.', '!', '?')

    /**
     * True when text typed next begins a sentence: at the start of the field,
     * on a new line, or after `.`, `!` or `?` followed by spaces.
     */
    fun startsSentence(textBeforeCursor: String): Boolean {
        val last = textBeforeCursor.lastOrNull() ?: return true
        if (last == '\n') return true
        if (last != ' ') return false
        val withoutSpaces = textBeforeCursor.trimEnd(' ')
        return withoutSpaces.isEmpty() || withoutSpaces.last() in TERMINATORS || withoutSpaces.last() == '\n'
    }
}
