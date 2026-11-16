package com.kboard.thekami.engine.layout

import com.kboard.thekami.engine.language.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberRowTest {
    private val settings = listOf(false, true)

    @Test
    fun rowCountMatchesTheLettersPage() {
        Language.entries.forEach { language ->
            settings.forEach { numberRow ->
                assertEquals(Layouts.rowCount(numberRow), Layouts.letters(language, numberRow).rows.size)
            }
        }
    }

    @Test
    fun theNumberRowAddsExactlyOneRow() {
        assertEquals(Layouts.rowCount(false) + 1, Layouts.rowCount(true))
    }

    @Test
    fun theNumberRowIsOnTopWithAllDigitsInOrder() {
        Language.entries.forEach { language ->
            val top = Layouts.letters(language, numberRow = true).rows.first()
            assertEquals("1234567890", top.filterIsInstance<Key.Character>().joinToString("") { it.base })
        }
    }

    @Test
    fun theNumberRowFillsTheSameWidthAsOtherRows() {
        Language.entries.forEach { language ->
            val top = Layouts.letters(language, numberRow = true).rows.first()
            assertEquals(ROW_WEIGHT, top.map { it.weight }.sum(), TOLERANCE)
        }
    }

    @Test
    fun theRowsBelowTheNumberRowAreTheNormalLettersPage() {
        Language.entries.forEach { language ->
            val withNumbers = Layouts.letters(language, numberRow = true).rows.drop(1)
            assertEquals(Layouts.letters(language, numberRow = false).rows, withNumbers)
        }
    }

    @Test
    fun symbolPagesAreNeverTallerThanTheLettersPage() {
        settings.forEach { numberRow ->
            listOf(SymbolLayouts.symbols, SymbolLayouts.moreSymbols).forEach { page ->
                assertTrue(page.rows.size <= Layouts.rowCount(numberRow))
            }
        }
    }

    @Test
    fun layoutForPassesTheNumberRowSettingToTheLettersPage() {
        Language.entries.forEach { language ->
            assertSame(
                Layouts.letters(language, numberRow = true),
                Layouts.layoutFor(language, Layer.LETTERS, numberRow = true),
            )
        }
    }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}
