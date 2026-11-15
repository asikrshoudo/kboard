package com.kboard.thekami.engine.suggest

/**
 * Phonetic spelling is ambiguous: o and O, i and I, t and T give different
 * Bangla. This lists the alternative spellings of a typed word, changing one
 * letter at a time. Vowels come first because they are the common mix-up.
 */
object BanglaVariants {
    private const val MAX_VARIANTS = 6
    private val VOWEL_SWAPS = mapOf('o' to 'O', 'O' to 'o', 'i' to 'I', 'I' to 'i', 'u' to 'U', 'U' to 'u')
    private val CONSONANT_SWAPS = mapOf('t' to 'T', 'T' to 't', 'd' to 'D', 'D' to 'd', 'n' to 'N', 'N' to 'n')

    fun of(latin: String): List<String> =
        (swapOneLetter(latin, VOWEL_SWAPS) + swapOneLetter(latin, CONSONANT_SWAPS))
            .distinct()
            .take(MAX_VARIANTS)

    private fun swapOneLetter(latin: String, swaps: Map<Char, Char>): List<String> =
        latin.indices.mapNotNull { index ->
            swaps[latin[index]]?.let { latin.replaceRange(index, index + 1, it.toString()) }
        }
}
