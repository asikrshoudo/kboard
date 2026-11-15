package com.kboard.thekami.engine.autocorrect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MisspellingTableTest {
    private val rules = MisspellingTable.rules

    @Test
    fun theTableIsNotEmpty() {
        assertTrue(rules.size > MIN_RULES)
    }

    @Test
    fun typosAreLowercaseAndDifferFromTheirFix() {
        rules.forEach { (typo, fix) ->
            assertEquals(typo, typo.lowercase(), typo)
            assertNotEquals(typo, typo, fix)
        }
    }

    @Test
    fun aFixIsNeverItselfATypo() {
        rules.values.forEach { assertFalse(it, it in rules) }
    }

    @Test
    fun wellKnownRulesExist() {
        assertEquals("the", rules["teh"])
        assertEquals("I", rules["i"])
        assertEquals("don't", rules["dont"])
    }

    private companion object {
        const val MIN_RULES = 100
    }
}
