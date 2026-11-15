package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.Layouts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BanglaInputTest {
    private val editor = FakeTextEditor()
    private val handler = KeyInputHandler(editor).apply { setLanguage(Language.BANGLA_PHONETIC) }

    private fun type(text: String) = text.forEach { handler.onKey(letterKey(it)) }

    @Test
    fun lettersAreShownAsBanglaWhileTyping() {
        type("ami")
        assertEquals("আমি", editor.content)
        assertEquals("compose:আমি", editor.events.last())
    }

    @Test
    fun spaceKeepsTheWordAndStartsANewOne() {
        type("ami")
        handler.onKey(controlKey(ControlAction.SPACE))
        type("a")
        assertEquals("আমি আ", editor.content)
    }

    @Test
    fun backspaceEditsTheWordBeingTyped() {
        type("am")
        handler.onKey(controlKey(ControlAction.BACKSPACE))
        assertEquals("আ", editor.content)
        handler.onKey(controlKey(ControlAction.BACKSPACE))
        assertEquals("", editor.content)
        handler.onKey(controlKey(ControlAction.BACKSPACE))
        assertEquals("delete", editor.events.last())
    }

    @Test
    fun shiftGivesTheCapitalLetterSound() {
        handler.onKey(controlKey(ControlAction.SHIFT))
        type("t")
        assertEquals("ট", editor.content)
    }

    @Test
    fun theDariKeyEndsTheWord() {
        type("ami")
        val dari = Layouts.letters(Language.BANGLA_PHONETIC).rows.flatten()
            .filterIsInstance<Key.Character>().first { it.base == "\u0964" }
        handler.onKey(dari)
        assertEquals("আমি\u0964", editor.content)
    }

    @Test
    fun switchingLanguageKeepsTheTypedWord() {
        type("ami")
        handler.onKey(controlKey(ControlAction.SWITCH_LANGUAGE))
        assertEquals(Language.ENGLISH_US, handler.language)
        type("a")
        assertEquals("আমিa", editor.content)
    }

    @Test
    fun movingTheCursorStartsANewWord() {
        type("a")
        editor.setText("XYZ")
        type("m")
        assertEquals("XYZম", editor.content)
        assertTrue("finish" in editor.events)
    }

    @Test
    fun digitsAndSymbolsAreTypedAsTheyAre() {
        handler.onKey(controlKey(ControlAction.SHOW_SYMBOLS))
        handler.onKey(Key.Character(base = "1", shifted = "1"))
        assertEquals("1", editor.content)
    }
}
