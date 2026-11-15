package com.kboard.thekami.engine.suggest

import com.kboard.thekami.engine.language.Language

/** A tappable suggestion. [replaceLength] is how many characters before the cursor it replaces. */
data class Suggestion(val text: String, val replaceLength: Int = 0)

/**
 * What the suggestion engine needs to know. [composingLatin] is the Latin
 * text of the Bangla word being typed, empty when no word is being composed.
 */
data class SuggestionRequest(
    val language: Language,
    val composingLatin: String,
    val textBeforeCursor: String,
)
