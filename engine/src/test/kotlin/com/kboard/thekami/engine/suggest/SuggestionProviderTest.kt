package com.kboard.thekami.engine.suggest

import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.phonetic.PhoneticTransliterator.transliterate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SuggestionProviderTest {
    private val store = FakeWordStore()

    private fun provider(seed: LanguageSeed = LanguageSeed.EMPTY) =
        SuggestionProvider(store, { seed }) { FakeWordStore.NOW }

    private suspend fun suggest(
        before: String,
        latin: String = "",
        seed: LanguageSeed = LanguageSeed.EMPTY,
        language: Language = Language.ENGLISH_US,
    ): List<Suggestion> = provider(seed).suggest(SuggestionRequest(language, latin, before))

    private suspend fun texts(
        before: String,
        latin: String = "",
        seed: LanguageSeed = LanguageSeed.EMPTY,
        language: Language = Language.ENGLISH_US,
    ) = suggest(before, latin, seed, language).map { it.text }

    @Test
    fun anEmptyStoreAndNoSeedSuggestsNothing() = runTest {
        assertEquals(emptyList<String>(), texts("hel"))
    }

    @Test
    fun completionsAreRankedByUse() = runTest {
        store.addWord("hello", uses = 5)
        store.addWord("help", uses = 2)
        store.addWord("helmet", uses = 1)
        assertEquals(listOf("hello", "help", "helmet"), texts("hel"))
    }

    @Test
    fun theWordAlreadyTypedIsNotSuggested() = runTest {
        store.addWord("hello", uses = 5)
        assertEquals(emptyList<String>(), texts("hello"))
    }

    @Test
    fun aCapitalPrefixGivesACapitalSuggestionThatReplacesThePrefix() = runTest {
        store.addWord("hello", uses = 5)
        assertEquals(listOf(Suggestion("Hello", replaceLength = 2)), suggest("He"))
    }

    @Test
    fun theNextWordIsPredictedAfterASpace() = runTest {
        store.addPair("good", "morning", uses = 3)
        assertEquals(listOf(Suggestion("morning", replaceLength = 0)), suggest("good "))
    }

    @Test
    fun noPredictionRightAfterAFullStop() = runTest {
        store.addPair("good", "morning", uses = 3)
        assertEquals(emptyList<String>(), texts("good. "))
    }

    @Test
    fun aWordThatFollowedThePreviousWordRanksHigher() = runTest {
        store.addWord("hello", uses = 3)
        store.addWord("help", uses = 4)
        store.addPair("say", "hello", uses = 2)
        assertEquals(listOf("hello", "help"), texts("say hel"))
    }

    @Test
    fun recentWordsBeatOldOnesWithTheSameUse() = runTest {
        store.addWord("helmet", uses = 3, lastUsedMs = FakeWordStore.NOW - OLD_MS)
        store.addWord("hello", uses = 3)
        assertEquals(listOf("hello", "helmet"), texts("hel"))
    }

    @Test
    fun atMostThreeSuggestionsAreShown() = runTest {
        listOf("hello", "help", "helmet", "helium", "helix").forEach { store.addWord(it, uses = 1) }
        assertEquals(3, suggest("hel").size)
    }

    @Test
    fun seedWordsAreSuggestedFromTheFirstLetterOnAFreshKeyboard() = runTest {
        val seed = LanguageSeed.parse("hello help helmet helium", "", "")
        assertEquals(listOf("hello", "help", "helmet"), texts("hel", seed = seed))
    }

    @Test
    fun aWordTheUserTypedBeatsEverySeedWord() = runTest {
        val seed = LanguageSeed.parse("hello help helmet helium", "", "")
        store.addWord("helmet", uses = 1)
        assertEquals(listOf("helmet", "hello", "help"), texts("hel", seed = seed))
    }

    @Test
    fun startersAreOfferedAtTheStartOfTheFieldInCapitals() = runTest {
        val seed = LanguageSeed.parse("", "", "i the thanks")
        assertEquals(listOf("I", "The", "Thanks"), texts("", seed = seed))
    }

    @Test
    fun startersMidSentenceStayLowercaseExceptTheWordI() = runTest {
        val seed = LanguageSeed.parse("", "", "i the thanks")
        assertEquals(listOf("I", "the", "thanks"), texts("go ", seed = seed))
    }

    @Test
    fun seedFollowersPredictTheNextWord() = runTest {
        val seed = LanguageSeed.parse("", "good: morning night", "i the")
        assertEquals(listOf("morning", "night"), texts("good ", seed = seed))
    }

    @Test
    fun banglaOffersTheDefaultAnAlternativeAndTheLatinText() = runTest {
        assertEquals(
            listOf(transliterate("tomake"), transliterate("tOmake"), "tomake"),
            texts(before = "", latin = "tomake", language = Language.BANGLA_PHONETIC),
        )
    }

    @Test
    fun aLearnedBanglaSpellingComesBeforeUnlearnedVariants() = runTest {
        store.addWord(transliterate("tomI"), uses = 2)
        assertEquals(
            listOf(transliterate("tomi"), transliterate("tomI"), "tomi"),
            texts(before = "", latin = "tomi", language = Language.BANGLA_PHONETIC),
        )
    }

    @Test
    fun banglaFallsBackToTheLatinTextWhenThereIsNoAlternative() = runTest {
        assertEquals(
            listOf(transliterate("kk"), "kk"),
            texts(before = "", latin = "kk", language = Language.BANGLA_PHONETIC),
        )
    }

    @Test
    fun banglaCompletionsComeFromTheSeedWords() = runTest {
        val seed = LanguageSeed.parse("আমি আমার আমরা", "", "")
        assertEquals(
            listOf("আমি", "আমার", "am"),
            texts(before = "", latin = "am", seed = seed, language = Language.BANGLA_PHONETIC),
        )
    }

    private companion object {
        const val OLD_MS = 365L * 24 * 60 * 60 * 1000
    }
}
