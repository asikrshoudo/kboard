package com.kboard.thekami.engine.suggest

/** The word being typed and the word before it, read from the text before the cursor. */
data class WordContext(val previous: String?, val current: String) {
    companion object {
        private val APOSTROPHES = setOf('\'', '\u2019')

        /** Letters and the marks that Bangla vowel signs, hasanta and nukta are made of. */
        internal fun isWordChar(char: Char): Boolean =
            char.isLetter() ||
                char.category == CharCategory.NON_SPACING_MARK ||
                char.category == CharCategory.COMBINING_SPACING_MARK

        internal fun isApostrophe(char: Char): Boolean = char in APOSTROPHES

        /**
         * [previous] is only set when it is separated from the current word by
         * spaces alone, so a comma or a full stop ends the sentence context.
         */
        fun parse(textBeforeCursor: String): WordContext {
            val currentStart = wordStart(textBeforeCursor, textBeforeCursor.length)
            val current = textBeforeCursor.substring(currentStart)
            val previousEnd = skipSpacesBackwards(textBeforeCursor, currentStart)
            val previousStart = wordStart(textBeforeCursor, previousEnd)
            val previous = textBeforeCursor.substring(previousStart, previousEnd).ifEmpty { null }
            return WordContext(previous, current)
        }

        private fun wordStart(text: String, end: Int): Int {
            var start = end
            while (start > 0 && isInsideWord(text, start - 1, end)) start--
            return start
        }

        private fun isInsideWord(text: String, index: Int, end: Int): Boolean {
            val char = text[index]
            if (isWordChar(char)) return true
            // An apostrophe joins letters (don't), but not at the very end of the text.
            return isApostrophe(char) && index > 0 && index < end - 1 && isWordChar(text[index - 1])
        }

        private fun skipSpacesBackwards(text: String, from: Int): Int {
            var position = from
            while (position > 0 && text[position - 1] == ' ') position--
            return position
        }
    }
}
