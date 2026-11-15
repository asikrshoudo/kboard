package com.kboard.thekami.engine.suggest

import org.junit.Assert.assertEquals
import org.junit.Test

class LanguageSeedTest {
    private val seed = LanguageSeed.parse(
        wordsText = "\n  hello help\n  world  ",
        followersText = "good: morning night\n\nthank: you",
        startersText = "i the",
    )

    @Test
    fun wordsKeepTheirOrderAndAreFilteredByPrefix() {
        assertEquals(listOf("hello", "help"), seed.wordsStartingWith("hel", 10))
        assertEquals(listOf("hello"), seed.wordsStartingWith("hel", 1))
        assertEquals(listOf("hello", "help", "world"), seed.wordsStartingWith("", 10))
    }

    @Test
    fun followersAreFoundByThePreviousWord() {
        assertEquals(listOf("morning", "night"), seed.followersOf("good"))
        assertEquals(emptyList<String>(), seed.followersOf("unknown"))
    }

    @Test
    fun startersAreParsed() {
        assertEquals(listOf("i", "the"), seed.starters)
    }

    @Test(expected = IllegalArgumentException::class)
    fun aFollowerLineWithoutAColonIsRejected() {
        LanguageSeed.parse("", "good morning", "")
    }
}
