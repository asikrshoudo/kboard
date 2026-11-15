package com.kboard.thekami.engine.autocorrect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AutoCorrectorTest {
    private var ignored = emptySet<String>()
    private val corrector = AutoCorrector { ignored }

    private fun fix(word: String, delimiter: String = " ") = corrector.correctionFor(word, delimiter)

    @Test
    fun aKnownTypoIsFixed() {
        assertEquals("the", fix("teh"))
        assertEquals("don't", fix("dont"))
        assertEquals("a lot", fix("alot"))
    }

    @Test
    fun theTypedCapitalizationIsKept() {
        assertEquals("The", fix("Teh"))
        assertEquals("THE", fix("TEH"))
        assertEquals("I'm", fix("Im"))
    }

    @Test
    fun correctAndUnknownWordsAreLeftAlone() {
        assertNull(fix("hello"))
        assertNull(fix("restaurant"))
        assertNull(fix("I"))
        assertNull(fix("the"))
        assertNull(fix(""))
    }

    @Test
    fun aWordTheUserKeptIsNeverFixedAgain() {
        ignored = setOf("teh")
        assertNull(fix("teh"))
        assertNull(fix("Teh"))
    }

    @Test
    fun aSingleLetterIsOnlyFixedBeforeASpaceOrNewLine() {
        assertEquals("I", fix("i", " "))
        assertEquals("I", fix("i", "\n"))
        assertNull(fix("i", "."))
        assertNull(fix("i", ","))
    }

    @Test
    fun longerWordsAreFixedBeforePunctuation() {
        assertEquals("the", fix("teh", ","))
        assertEquals("the", fix("teh", "."))
    }
}
