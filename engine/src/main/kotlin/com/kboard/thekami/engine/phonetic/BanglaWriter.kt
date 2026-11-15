package com.kboard.thekami.engine.phonetic

/**
 * Builds Bangla text from tokens and remembers what was written last, which
 * decides whether a vowel becomes a sign or a letter and whether two
 * consonants are joined with a hasanta into a conjunct.
 */
internal class BanglaWriter {
    private enum class State { OTHER, VOWEL, CONSONANT, CLOSED_CONSONANT }

    private val out = StringBuilder()
    private var state = State.OTHER

    fun write(token: Token) {
        when (token) {
            is Token.Consonant -> consonant(token.text, token.joins)
            is Token.YaConsonant -> ya()
            is Token.Vowel -> vowel(token)
            is Token.InherentO -> inherentO()
            is Token.Mark -> plain(token.text)
            is Token.Literal -> plain(token.text)
        }
    }

    override fun toString(): String = out.toString()

    private fun isConsonant() = state == State.CONSONANT || state == State.CLOSED_CONSONANT

    private fun consonant(text: String, joins: Boolean) {
        if (state == State.CONSONANT) out.append(HASANTA)
        out.append(text)
        state = if (joins) State.CONSONANT else State.CLOSED_CONSONANT
    }

    private fun ya() {
        if (state == State.CONSONANT) consonant(YA, joins = true) else consonant(YA + NUKTA, joins = false)
    }

    private fun vowel(token: Token.Vowel) {
        out.append(if (isConsonant()) token.sign else token.independent)
        state = State.VOWEL
    }

    /** After a consonant, o writes nothing but ends any conjunct (ক + o + ত is কত). */
    private fun inherentO() {
        if (isConsonant()) {
            state = State.OTHER
        } else {
            out.append(if (state == State.VOWEL) O_LETTER else A_LETTER)
            state = State.VOWEL
        }
    }

    private fun plain(text: String) {
        out.append(text)
        state = State.OTHER
    }

    private companion object {
        const val YA = "য"
        const val O_LETTER = "ও"
        const val A_LETTER = "অ"
    }
}
