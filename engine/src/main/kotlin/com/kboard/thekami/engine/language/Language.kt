package com.kboard.thekami.engine.language

/**
 * A typing language the keyboard can switch to. [composes] is true when typed
 * letters are collected and converted (transliterated) before they reach the
 * text field, instead of being committed one by one.
 */
enum class Language(val id: String, val nativeName: String, val composes: Boolean) {
    ENGLISH_US(id = "en-US", nativeName = "English (US)", composes = false),
    BANGLA_PHONETIC(id = "bn-BD-phonetic", nativeName = "বাংলা", composes = true),
    ;

    /** The language after this one, wrapping around. */
    fun next(): Language = entries[(ordinal + 1) % entries.size]

    companion object {
        fun fromId(id: String): Language = entries.firstOrNull { it.id == id } ?: ENGLISH_US
    }
}
