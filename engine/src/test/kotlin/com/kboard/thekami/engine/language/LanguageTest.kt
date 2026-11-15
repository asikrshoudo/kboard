package com.kboard.thekami.engine.language

import org.junit.Assert.assertEquals
import org.junit.Test

class LanguageTest {
    @Test
    fun nextCyclesThroughEveryLanguageAndWraps() {
        var language = Language.entries.first()
        repeat(Language.entries.size) { language = language.next() }
        assertEquals(Language.entries.first(), language)
    }

    @Test
    fun fromIdFindsALanguageByItsId() {
        Language.entries.forEach { assertEquals(it, Language.fromId(it.id)) }
    }

    @Test
    fun unknownIdFallsBackToEnglish() {
        assertEquals(Language.ENGLISH_US, Language.fromId("xx"))
        assertEquals(Language.ENGLISH_US, Language.fromId(""))
    }
}
