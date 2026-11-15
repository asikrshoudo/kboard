package com.kboard.thekami.engine.layout

import com.kboard.thekami.engine.language.Language

/** Chooses the page to show. Letters follow the language; symbol pages are shared. */
object Layouts {
    private const val DARI = "\u0964"

    private val englishUs = qwerty(period = PERIOD)

    // Bangla phonetic is typed on Latin keys; only the full stop becomes the dari.
    private val banglaPhonetic = qwerty(period = Key.Character(base = DARI, shifted = DARI))

    fun letters(language: Language): KeyboardLayout = when (language) {
        Language.ENGLISH_US -> englishUs
        Language.BANGLA_PHONETIC -> banglaPhonetic
    }

    fun layoutFor(language: Language, layer: Layer): KeyboardLayout = when (layer) {
        Layer.LETTERS -> letters(language)
        Layer.SYMBOLS -> SymbolLayouts.symbols
        Layer.MORE_SYMBOLS -> SymbolLayouts.moreSymbols
    }

    private fun qwerty(period: Key.Character) = KeyboardLayout(
        rows = listOf(
            letterKeys("qwertyuiop"),
            buildList {
                add(Key.Spacer(HALF_KEY))
                addAll(letterKeys("asdfghjkl"))
                add(Key.Spacer(HALF_KEY))
            },
            thirdRow(ControlAction.SHIFT, letterKeys("zxcvbnm")),
            bottomRow(ControlAction.SHOW_SYMBOLS, period),
        ),
    )
}
