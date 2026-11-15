package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.phonetic.PhoneticComposer
import com.kboard.thekami.engine.suggest.LearningPolicy
import com.kboard.thekami.engine.suggest.WordContext

/** Ends the word being typed and reports it to the learner when [allowed]. */
internal class WordCompleter(
    private val editor: TextEditor,
    private val composer: PhoneticComposer,
    private val learner: WordLearner,
) {
    var allowed = false

    fun finishWord() {
        composer.finish()
        if (!allowed) return
        val before = editor.textBeforeCursor(LOOKBEHIND_CHARS) ?: return
        val context = WordContext.parse(before)
        if (LearningPolicy.isLearnableWord(context.current)) {
            learner.onWordCompleted(context.previous?.lowercase(), context.current.lowercase())
        }
    }

    private companion object {
        /** Long enough for the last word and the one before it. */
        const val LOOKBEHIND_CHARS = 64
    }
}
