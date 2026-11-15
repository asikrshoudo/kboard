package com.kboard.thekami.engine.suggest

import com.kboard.thekami.engine.language.Language

/** The built-in word list of each language. A new language must add one here. */
object Seeds {
    fun of(language: Language): LanguageSeed = when (language) {
        Language.ENGLISH_US -> EnglishSeed.seed
        Language.BANGLA_PHONETIC -> BanglaSeed.seed
    }
}
