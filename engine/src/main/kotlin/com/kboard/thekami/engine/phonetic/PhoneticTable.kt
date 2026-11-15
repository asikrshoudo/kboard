package com.kboard.thekami.engine.phonetic

internal const val HASANTA = "\u09CD"
internal const val NUKTA = "\u09BC"

internal sealed interface Token {
    /** A consonant. [joins] is false for letters that never start a conjunct (ড় ঢ় য়). */
    data class Consonant(val text: String, val joins: Boolean = true) : Token

    data class Vowel(val independent: String, val sign: String) : Token

    data class Mark(val text: String) : Token

    data class Literal(val text: String) : Token

    /** The letter o: the vowel inherent in a consonant, written as nothing. */
    data object InherentO : Token

    /** The letter y: য় on its own, or the ya-phala (্য) after a consonant. */
    data object YaConsonant : Token
}

internal class Match(val token: Token, val length: Int)

/**
 * Phonetic rules in the style of Avro Phonetic. Capital letters matter:
 * t is ত but T is ট, d is দ but D is ড, S is শ but Sh is ষ. Uppercase letters
 * without a rule of their own act like the lowercase letter.
 */
internal object PhoneticTable {
    private const val LONGEST_RULE = 3
    private const val ANUSVARA = "\u0982"

    private val consonants: Map<String, Token> = mapOf(
        "k" to "ক", "kh" to "খ", "g" to "গ", "gh" to "ঘ", "Ng" to "ঙ",
        "c" to "চ", "ch" to "ছ", "j" to "জ", "J" to "জ", "jh" to "ঝ", "NG" to "ঞ",
        "T" to "ট", "Th" to "ঠ", "D" to "ড", "Dh" to "ঢ", "N" to "ণ",
        "t" to "ত", "th" to "থ", "d" to "দ", "dh" to "ধ", "n" to "ন",
        "p" to "প", "ph" to "ফ", "f" to "ফ", "b" to "ব", "bh" to "ভ", "v" to "ভ", "m" to "ম",
        "z" to "য", "r" to "র", "l" to "ল", "sh" to "শ", "S" to "শ", "Sh" to "ষ", "s" to "স", "h" to "হ",
        "q" to "ক", "x" to "ক${HASANTA}স",
    ).mapValues { Token.Consonant(it.value) }

    private val closedConsonants: Map<String, Token> = mapOf(
        "R" to "ড$NUKTA",
        "Rh" to "ঢ$NUKTA",
    ).mapValues { Token.Consonant(it.value, joins = false) }

    private val vowels: Map<String, Token> = mapOf(
        "a" to Token.Vowel("আ", "া"),
        "i" to Token.Vowel("ই", "ি"),
        "I" to Token.Vowel("ঈ", "ী"),
        "u" to Token.Vowel("উ", "ু"),
        "U" to Token.Vowel("ঊ", "ূ"),
        "e" to Token.Vowel("এ", "ে"),
        "oi" to Token.Vowel("ঐ", "ৈ"),
        "OI" to Token.Vowel("ঐ", "ৈ"),
        "O" to Token.Vowel("ও", "ো"),
        "w" to Token.Vowel("ও", "ো"),
        "ou" to Token.Vowel("ঔ", "ৌ"),
        "OU" to Token.Vowel("ঔ", "ৌ"),
        "rri" to Token.Vowel("ঋ", "ৃ"),
    )

    private val others: Map<String, Token> = mapOf(
        "o" to Token.InherentO,
        "y" to Token.YaConsonant,
        "ng" to Token.Mark(ANUSVARA),
    )

    private val tokens: Map<String, Token> = consonants + closedConsonants + vowels + others

    /** The longest rule that matches at [index]; unknown characters pass through unchanged. */
    fun matchAt(text: String, index: Int): Match {
        val longest = (minOf(LONGEST_RULE, text.length - index) downTo 1).firstNotNullOfOrNull { length ->
            tokens[text.substring(index, index + length)]?.let { Match(it, length) }
        }
        return longest ?: fallback(text[index])
    }

    private fun fallback(char: Char): Match =
        Match(tokens[char.lowercase()] ?: Token.Literal(char.toString()), 1)
}
