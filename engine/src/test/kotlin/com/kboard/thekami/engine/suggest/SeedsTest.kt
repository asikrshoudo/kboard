package com.kboard.thekami.engine.suggest

import com.kboard.thekami.engine.language.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedsTest {
    private fun allWords(language: Language) = Seeds.of(language).wordsStartingWith("", Int.MAX_VALUE)

    @Test
    fun everyLanguageHasWordsAndStarters() {
        Language.entries.forEach { language ->
            assertTrue(language.id, allWords(language).isNotEmpty())
            assertTrue(language.id, Seeds.of(language).starters.isNotEmpty())
        }
    }

    @Test
    fun englishHasWordsForEveryLetterExceptX() {
        val seed = Seeds.of(Language.ENGLISH_US)
        (('a'..'z') - 'x').forEach { letter ->
            val count = seed.wordsStartingWith(letter.toString(), Int.MAX_VALUE).size
            assertTrue("$letter has only $count words", count >= MIN_WORDS_PER_LETTER)
        }
    }

    @Test
    fun noSeedWordIsListedTwice() {
        Language.entries.forEach { language ->
            val words = allWords(language)
            assertEquals(language.id, words.size, words.toSet().size)
        }
    }

    @Test
    fun englishWordsAreLowercaseWithoutSpaces() {
        allWords(Language.ENGLISH_US).forEach { assertEquals(it, it.lowercase(), it) }
    }

    @Test
    fun banglaWordsUseOnlyBanglaCharacters() {
        allWords(Language.BANGLA_PHONETIC).forEach { word ->
            assertTrue(word, word.all { it.code in BANGLA_BLOCK })
        }
    }

    private companion object {
        const val MIN_WORDS_PER_LETTER = 5
        val BANGLA_BLOCK = 0x0980..0x09FF
    }
}
