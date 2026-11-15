package com.kboard.thekami.engine.phonetic

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneticTransliteratorTest {
    private val hasanta = "\u09CD"
    private val nukta = "\u09BC"

    private fun check(expected: String, latin: String) =
        assertEquals(latin, expected, PhoneticTransliterator.transliterate(latin))

    @Test
    fun emptyInputGivesEmptyOutput() = check("", "")

    @Test
    fun consonantsAndVowelSigns() {
        check("আমি", "ami")
        check("তুমি", "tumi")
        check("কিতাব", "kitab")
    }

    @Test
    fun capitalLettersChangeTheSound() {
        check("ভাষা", "bhaSha")
        check("ট", "T")
        check("ত", "t")
    }

    @Test
    fun aLeadingCapitalActsLikeTheLowercaseLetter() = check("আমি", "Ami")

    @Test
    fun inherentOWritesNothingAndLargeOMakesTheSign() {
        check("কত", "kot")
        check("ভাল", "bhalo")
        check("ভালো", "valO")
    }

    @Test
    fun adjacentConsonantsFormConjuncts() {
        check("র" + "ক" + hasanta + "ত", "rokto")
        check("ক" + hasanta + "ষ", "kSh")
    }

    @Test
    fun yaPhalaAfterAConsonantAndYaOtherwise() {
        check("ক" + hasanta + "য" + "া", "kya")
        check("আ" + "য" + nukta, "ay")
    }

    @Test
    fun rrAndRhUseTheNuktaForms() = check("বা" + "ড" + nukta + "ি", "baRi")

    @Test
    fun anusvaraComesFromNg() = check("বাংলা", "bangla")

    @Test
    fun vowelsAfterVowelsAreLetters() {
        check("আই", "ai")
        check("ঐ", "oi")
    }

    @Test
    fun vocalicRiSign() = check("ক" + "ৃ", "krri")
}
