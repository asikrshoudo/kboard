package com.kboard.thekami.engine.layout

import com.kboard.thekami.engine.language.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NumpadLayoutTest {
    private val rows = NumpadLayout.numpad.rows

    @Test
    fun everyRowFillsTheSameWidth() {
        rows.forEach { row -> assertEquals(ROW_WEIGHT, row.map { it.weight }.sum(), TOLERANCE) }
    }

    @Test
    fun hasAsManyRowsAsTheLettersPage() {
        assertEquals(Layouts.rowCount(numberRow = false), rows.size)
    }

    @Test
    fun hasEachDigitExactlyOnce() {
        val typed = rows.flatten().filterIsInstance<Key.Character>().map { it.base }
        assertEquals("0123456789".map { it.toString() }, typed.filter { it.single().isDigit() }.sorted())
    }

    @Test
    fun hasBackspaceEnterAndAWayBackToTheLetters() {
        val actions = rows.flatten().filterIsInstance<Key.Control>().map { it.action }
        assertTrue(actions.containsAll(listOf(ControlAction.BACKSPACE, ControlAction.ENTER, ControlAction.SHOW_LETTERS)))
    }

    @Test
    fun theSamePadIsUsedInEveryLanguage() {
        Language.entries.forEach { language ->
            assertSame(NumpadLayout.numpad, Layouts.layoutFor(language, Layer.NUMPAD))
        }
    }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}
