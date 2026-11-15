package com.kboard.thekami.engine.layout

import com.kboard.thekami.engine.language.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SymbolLayoutsTest {
    private val pages = listOf(SymbolLayouts.symbols, SymbolLayouts.moreSymbols)

    @Test
    fun rowsFillTheSameWidthAsTheLettersPage() {
        pages.forEach { page ->
            page.rows.forEach { row -> assertEquals(ROW_WEIGHT, row.map { it.weight }.sum(), TOLERANCE) }
        }
    }

    @Test
    fun pagesHaveTheSameRowCountSoTheKeyboardKeepsItsHeight() {
        val letterRows = Layouts.letters(Language.ENGLISH_US).rows.size
        pages.forEach { assertEquals(letterRows, it.rows.size) }
    }

    @Test
    fun firstPageHasAllDigits() {
        assertTrue(typedTexts(SymbolLayouts.symbols).containsAll("0123456789".map { it.toString() }))
    }

    @Test
    fun secondPageHasTheTakaSign() {
        assertTrue("৳" in typedTexts(SymbolLayouts.moreSymbols))
    }

    @Test
    fun layoutForReturnsThePageOfEachLayer() {
        Language.entries.forEach { language ->
            assertSame(Layouts.letters(language), Layouts.layoutFor(language, Layer.LETTERS))
            assertSame(SymbolLayouts.symbols, Layouts.layoutFor(language, Layer.SYMBOLS))
            assertSame(SymbolLayouts.moreSymbols, Layouts.layoutFor(language, Layer.MORE_SYMBOLS))
        }
    }

    private fun typedTexts(layout: KeyboardLayout): List<String> =
        layout.rows.flatten().filterIsInstance<Key.Character>().map { it.base }

    private companion object {
        const val TOLERANCE = 0.001f
    }
}
