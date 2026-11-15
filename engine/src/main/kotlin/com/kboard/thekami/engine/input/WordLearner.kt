package com.kboard.thekami.engine.input

/** Told about every word the user finishes, when learning is allowed. */
fun interface WordLearner {
    /** [previous] is the word before [word] in the same sentence, if any. Both are lowercase. */
    fun onWordCompleted(previous: String?, word: String)

    companion object {
        val None = WordLearner { _, _ -> }
    }
}
