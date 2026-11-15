package com.kboard.thekami.engine.input

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SentenceCaseTest {
    @Test
    fun emptyFieldStartsASentence() = assertTrue(SentenceCase.startsSentence(""))

    @Test
    fun midWordDoesNot() = assertFalse(SentenceCase.startsSentence("Hello"))

    @Test
    fun afterAWordAndSpaceDoesNot() = assertFalse(SentenceCase.startsSentence("Hello "))

    @Test
    fun afterSentenceEndAndSpaceDoes() = assertTrue(SentenceCase.startsSentence("Hello. "))

    @Test
    fun afterSeveralSpacesDoes() = assertTrue(SentenceCase.startsSentence("Really?!  "))

    @Test
    fun rightAfterThePeriodDoesNot() = assertFalse(SentenceCase.startsSentence("Hello."))

    @Test
    fun afterANewlineDoes() = assertTrue(SentenceCase.startsSentence("Hello\n"))

    @Test
    fun afterANewlineAndSpaceDoes() = assertTrue(SentenceCase.startsSentence("Hello\n "))
}
