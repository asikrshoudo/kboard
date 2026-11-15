package com.kboard.thekami.engine.suggest

import org.junit.Assert.assertEquals
import org.junit.Test

class WordContextTest {
    private fun check(text: String, previous: String?, current: String) =
        assertEquals(text, WordContext(previous, current), WordContext.parse(text))

    @Test
    fun emptyText() = check("", null, "")

    @Test
    fun aSingleWord() = check("Hello", null, "Hello")

    @Test
    fun aWordAfterAnotherWord() = check("Hello wor", "Hello", "wor")

    @Test
    fun afterASpaceTheCurrentWordIsEmpty() = check("Hello ", "Hello", "")

    @Test
    fun aCommaEndsTheContext() = check("Hello, wor", null, "wor")

    @Test
    fun aFullStopEndsTheContext() = check("Hi.  ", null, "")

    @Test
    fun anApostropheJoinsLetters() = check("don't sto", "don't", "sto")

    @Test
    fun aTrailingApostropheIsNotPartOfTheWord() = check("dog'", null, "")

    @Test
    fun banglaVowelSignsBelongToTheWord() = check("আমি তোমা", "আমি", "তোমা")

    @Test
    fun digitsAreNotWordCharacters() = check("abc123", null, "")
}
