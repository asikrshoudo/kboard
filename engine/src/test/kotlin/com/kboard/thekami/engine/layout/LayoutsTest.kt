package com.kboard.thekami.engine.layout

import com.kboard.thekami.engine.language.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutsTest {
    private fun rows(language: Language) = Layouts.letters(language).rows

    @Test
    fun everyRowFillsTheSameWidth() {
        Language.entries.forEach { language ->
            rows(language).forEach { row -> assertEquals(ROW_WEIGHT, row.map { it.weight }.sum(), TOLERANCE) }
        }
    }

    @Test
    fun containsEachLatinLetterOnceWithMatchingUppercase() {
        Language.entries.forEach { language ->
            val letters = rows(language).flatten().filterIsInstance<Key.Character>()
                .filter { it.base.single() in 'a'..'z' }
            assertEquals(('a'..'z').map { it.toString() }, letters.map { it.base }.sorted())
            letters.forEach { assertEquals(it.base.uppercase(), it.shifted) }
        }
    }

    @Test
    fun firstRowIsQwerty() {
        Language.entries.forEach { language ->
            val first = rows(language)[0].filterIsInstance<Key.Character>().joinToString("") { it.base }
            assertEquals("qwertyuiop", first)
        }
    }

    @Test
    fun lettersPageHasTheEssentialControls() {
        val actions = rows(Language.ENGLISH_US).flatten().filterIsInstance<Key.Control>().map { it.action }
        val expected = setOf(
            ControlAction.SHIFT,
            ControlAction.BACKSPACE,
            ControlAction.SPACE,
            ControlAction.ENTER,
            ControlAction.SHOW_SYMBOLS,
            ControlAction.SWITCH_LANGUAGE,
        )
        assertEquals(expected, actions.toSet())
    }

    @Test
    fun banglaUsesTheDariAsItsFullStop() {
        val keys = rows(Language.BANGLA_PHONETIC).flatten().filterIsInstance<Key.Character>().map { it.base }
        assertTrue("\u0964" in keys)
        assertFalse("." in keys)
    }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}
