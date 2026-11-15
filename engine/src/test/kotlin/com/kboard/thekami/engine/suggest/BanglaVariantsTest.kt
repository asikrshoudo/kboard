package com.kboard.thekami.engine.suggest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BanglaVariantsTest {
    @Test
    fun vowelVariantsComeBeforeConsonantVariants() {
        assertEquals(listOf("tOmake", "Tomake"), BanglaVariants.of("tomake"))
        assertEquals(listOf("tOmi", "tomI", "Tomi"), BanglaVariants.of("tomi"))
    }

    @Test
    fun lettersWithoutAnAlternativeGiveNothing() {
        assertEquals(emptyList<String>(), BanglaVariants.of("kk"))
        assertEquals(emptyList<String>(), BanglaVariants.of(""))
    }

    @Test
    fun noVariantEqualsTheOriginal() {
        assertFalse("tomake" in BanglaVariants.of("tomake"))
    }

    @Test
    fun theListIsCapped() {
        assertTrue(BanglaVariants.of("totototototototo").size <= MAX)
    }

    private companion object {
        const val MAX = 6
    }
}
