package com.kboard.thekami.engine.phonetic

/** Converts phonetically typed Latin letters to Bangla. See [PhoneticTable] for the rules. */
object PhoneticTransliterator {
    fun transliterate(latin: String): String {
        val writer = BanglaWriter()
        var index = 0
        while (index < latin.length) {
            val match = PhoneticTable.matchAt(latin, index)
            writer.write(match.token)
            index += match.length
        }
        return writer.toString()
    }
}
