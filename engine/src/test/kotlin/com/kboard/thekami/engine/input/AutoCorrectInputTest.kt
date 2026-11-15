package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoCorrectInputTest {
    private val editor = FakeTextEditor()
    private val rejected = mutableListOf<String>()
    private val learned = mutableListOf<Pair<String?, String>>()
    private val handler = KeyInputHandler(editor, corrections = { word -> rejected += word }) { previous, word ->
        learned += previous to word
    }
    private val space = controlKey(ControlAction.SPACE)
    private val backspace = controlKey(ControlAction.BACKSPACE)
    private val comma = Key.Character(base = ",", shifted = ",")
    private val period = Key.Character(base = ".", shifted = ".")

    init {
        handler.autocorrectAllowed = true
        handler.learningAllowed = true
    }

    private fun type(text: String) = text.forEach { handler.onKey(if (it == ' ') space else letterKey(it)) }

    @Test
    fun aKnownTypoIsFixedWhenSpaceIsTyped() {
        type("teh ")
        assertEquals("the ", editor.content)
    }

    @Test
    fun theTypedCapitalIsKept() {
        type("Teh ")
        assertEquals("The ", editor.content)
    }

    @Test
    fun aCorrectWordIsLeftAlone() {
        type("hello ")
        assertEquals("hello ", editor.content)
    }

    @Test
    fun nothingIsFixedUntilAutocorrectIsAllowed() {
        handler.autocorrectAllowed = false
        type("teh ")
        assertEquals("teh ", editor.content)
    }

    @Test
    fun backspaceRightAfterAFixBringsBackTheTypedWord() {
        type("teh ")
        handler.onKey(backspace)
        assertEquals("teh", editor.content)
        assertEquals(listOf("teh"), rejected)
    }

    @Test
    fun aWordTheUserKeptIsNotFixedAgain() {
        type("teh ")
        handler.onKey(backspace)
        type(" ")
        assertEquals("teh ", editor.content)
    }

    @Test
    fun theSecondBackspaceDeletesNormally() {
        type("teh ")
        handler.onKey(backspace)
        handler.onKey(backspace)
        assertEquals("te", editor.content)
    }

    @Test
    fun afterTypingMoreTheFixCanNoLongerBeUndone() {
        type("teh x")
        handler.onKey(backspace)
        assertEquals("the ", editor.content)
        assertTrue(rejected.isEmpty())
    }

    @Test
    fun punctuationAlsoTriggersTheFix() {
        type("teh")
        handler.onKey(comma)
        assertEquals("the,", editor.content)
    }

    @Test
    fun aSingleLetterIIsFixedBeforeASpaceButNotBeforeAFullStop() {
        type("i ")
        assertEquals("I ", editor.content)
        editor.setText("")
        type("i")
        handler.onKey(period)
        assertEquals("i.", editor.content)
    }

    @Test
    fun enterFixesTheWordButTheFixCannotBeUndone() {
        type("teh")
        handler.onKey(controlKey(ControlAction.ENTER))
        assertEquals("the", editor.content)
        assertEquals("enter", editor.events.last())
        handler.onKey(backspace)
        assertEquals("th", editor.content)
        assertTrue(rejected.isEmpty())
    }

    @Test
    fun banglaTypingIsNeverAutoCorrected() {
        handler.setLanguage(Language.BANGLA_PHONETIC)
        type("teh ")
        assertFalse(editor.content.contains("the"))
        assertTrue(rejected.isEmpty())
    }

    @Test
    fun ignoredWordsAreNeverFixed() {
        handler.ignoredCorrections = setOf("teh")
        type("teh ")
        assertEquals("teh ", editor.content)
    }

    @Test
    fun theFixedWordIsLearnedNotTheTypo() {
        type("teh ")
        assertEquals(listOf<Pair<String?, String>>(null to "the"), learned)
    }

    @Test
    fun aTypoWithTwoWordsIsFixed() {
        type("alot ")
        assertEquals("a lot ", editor.content)
    }
}
