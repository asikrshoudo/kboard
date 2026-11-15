package com.kboard.thekami.engine.input

/** Told when the user undoes an automatic correction, so the word is not corrected again. */
fun interface CorrectionListener {
    /** [word] is the lowercase word the user typed and wants to keep. */
    fun onCorrectionRejected(word: String)

    companion object {
        val None = CorrectionListener { }
    }
}
