package com.kboard.thekami.engine.suggest

/** The privacy rules for learning and suggesting. Every rule here is covered by a test. */
object LearningPolicy {
    private const val MIN_WORD_LENGTH = 2
    private const val MAX_WORD_LENGTH = 32

    /** Learning needs a normal field, no request to opt out, and incognito mode off. */
    fun isLearningAllowed(sensitiveField: Boolean, noPersonalizedLearning: Boolean, incognito: Boolean): Boolean =
        !sensitiveField && !noPersonalizedLearning && !incognito

    /** Passwords, email addresses and web addresses never get suggestions either. */
    fun isSuggestingAllowed(sensitiveField: Boolean): Boolean = !sensitiveField

    /** Only real words are kept: letters and Bangla marks, no digits, no very short or long strings. */
    fun isLearnableWord(word: String): Boolean =
        word.length in MIN_WORD_LENGTH..MAX_WORD_LENGTH &&
            word.all { WordContext.isWordChar(it) || WordContext.isApostrophe(it) }
}
