package com.kboard.thekami.engine.layout

import org.junit.Assert.assertEquals
import org.junit.Test

class EnglishUsLayoutTest {
    private val rows = EnglishUsLayout.layout.rows

    @Test
    fun everyRowFillsTheSameWidth() {
        rows.forEach { row ->
            assertEquals(EnglishUsLayout.ROW_WEIGHT, row.map { it.weight }.sum(), TOLERANCE)
        }
    }

    @Test
    fun containsEachLetterOnceWithMatchingUppercase() {
        val letters = rows.flatten().filterIsInstance<Key.Character>().filter { it.base[0].isLetter() }
        assertEquals(('a'..'z').map { it.toString() }, letters.map { it.base }.sorted())
        letters.forEach { assertEquals(it.base.uppercase(), it.shifted) }
    }

    @Test
    fun firstRowIsQwerty() {
        val first = rows[0].filterIsInstance<Key.Character>().joinToString("") { it.base }
        assertEquals("qwertyuiop", first)
    }

    @Test
    fun hasShiftBackspaceSpaceAndEnter() {
        val actions = rows.flatten().filterIsInstance<Key.Control>().map { it.action }
        assertEquals(ControlAction.entries.toSet(), actions.toSet())
    }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}
