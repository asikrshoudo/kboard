package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.phonetic.PhoneticTransliterator
import com.kboard.thekami.engine.suggest.Suggestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningTest {
    private val editor = FakeTextEditor()
    private val learned = mutableListOf<Pair<String?, String>>()
    private val handler = KeyInputHandler(editor) { previous, word -> learned += previous to word }
        .apply { learningAllowed = true }
    private val space = controlKey(ControlAction.SPACE)

    private fun type(text: String) = text.forEach { handler.onKey(if (it == ' ') space else letterKey(it)) }

    @Test
    fun aWordFinishedBySpaceIsLearned() {
        type("hello ")
        assertEquals(listOf<Pair<String?, String>>(null to "hello"), learned)
    }

    @Test
    fun theWordBeforeIsRememberedAsPrevious() {
        type("go home ")
        assertEquals(listOf<Pair<String?, String>>(null to "go", "go" to "home"), learned)
    }

    @Test
    fun nothingIsLearnedWhileLearningIsNotAllowed() {
        handler.learningAllowed = false
        type("hello world ")
        assertTrue(learned.isEmpty())
    }

    @Test
    fun learningIsOffByDefault() {
        val plain = KeyInputHandler(editor) { previous, word -> learned += previous to word }
        assertEquals(false, plain.learningAllowed)
    }

    @Test
    fun punctuationEndsAWord() {
        type("hello")
        handler.onKey(Key.Character(base = ",", shifted = ","))
        assertEquals(listOf<Pair<String?, String>>(null to "hello"), learned)
    }

    @Test
    fun shortWordsAndNumbersAreIgnored() {
        type("a ")
        handler.onKey(Key.Character(base = "1", shifted = "1"))
        handler.onKey(Key.Character(base = "2", shifted = "2"))
        handler.onKey(space)
        assertTrue(learned.isEmpty())
    }

    @Test
    fun wordsAreLearnedInLowercase() {
        handler.onKey(controlKey(ControlAction.SHIFT))
        type("hello ")
        assertEquals(listOf<Pair<String?, String>>(null to "hello"), learned)
    }

    @Test
    fun aBanglaWordIsLearnedWhenFinished() {
        handler.setLanguage(Language.BANGLA_PHONETIC)
        type("ami ")
        assertEquals(listOf<Pair<String?, String>>(null to PhoneticTransliterator.transliterate("ami")), learned)
    }

    @Test
    fun acceptingAnEnglishSuggestionReplacesThePrefixAndLearnsTheWord() {
        type("hel")
        handler.acceptSuggestion(Suggestion(text = "hello", replaceLength = 3))
        assertEquals("hello ", editor.content)
        assertEquals(listOf<Pair<String?, String>>(null to "hello"), learned)
    }

    @Test
    fun acceptingABanglaSuggestionReplacesTheComposingWord() {
        handler.setLanguage(Language.BANGLA_PHONETIC)
        type("tomake")
        handler.acceptSuggestion(Suggestion(text = "তোমাকে"))
        assertEquals("তোমাকে ", editor.content)
        assertEquals(listOf<Pair<String?, String>>(null to "তোমাকে"), learned)
        assertEquals("", handler.composingLatin)
    }

    @Test
    fun composingLatinIsExposedWhileTypingBangla() {
        handler.setLanguage(Language.BANGLA_PHONETIC)
        type("am")
        assertEquals("am", handler.composingLatin)
        type(" ")
        assertEquals("", handler.composingLatin)
    }
}
