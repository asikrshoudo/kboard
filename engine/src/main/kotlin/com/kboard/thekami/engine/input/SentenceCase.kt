package com.kboard.thekami.engine.input

/** Decides when the next letter should be capitalized. */
object SentenceCase {
    private val TERMINATORS = setOf('.', '!', '?')

    /**
     * True when text typed next begins a sentence: at the start of the field,
     * on a new line, or after `.`, `!` or `?` followed by spaces.
     */
    fun startsSentence(textBeforeCursor: String): Boolean {
        val withoutSpaces = textBeforeCursor.trimEnd(' ')
        val endsWithSpace = withoutSpaces.length < textBeforeCursor.length
        val last = withoutSpaces.lastOrNull() ?: return true
        return last == '\n' || (endsWithSpace && last in TERMINATORS)
    }
}
