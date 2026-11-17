package com.kboard.thekami.engine.layout

import com.kboard.thekami.engine.language.Language

/** Chooses the page to show. Letters follow the language; symbol and number pages are shared. */
object Layouts {
    private const val DARI = "\u0964"

    /** Rows of the letters page without the number row. */
    private const val LETTER_ROWS = 4

    private val dari = Key.Character(base = DARI, shifted = DARI)

    private val englishUs = qwerty(PERIOD, withNumberRow = false)
    private val englishUsWithNumbers = qwerty(PERIOD, withNumberRow = true)

    // Bangla phonetic is typed on Latin keys; only the full stop becomes the dari.
    private val banglaPhonetic = qwerty(dari, withNumberRow = false)
    private val banglaPhoneticWithNumbers = qwerty(dari, withNumberRow = true)

    fun letters(language: Language, numberRow: Boolean = false): KeyboardLayout = when (language) {
        Language.ENGLISH_US -> if (numberRow) englishUsWithNumbers else englishUs
        Language.BANGLA_PHONETIC -> if (numberRow) banglaPhoneticWithNumbers else banglaPhonetic
    }

    fun layoutFor(language: Language, layer: Layer, numberRow: Boolean = false): KeyboardLayout = when (layer) {
        Layer.LETTERS -> letters(language, numberRow)
        Layer.SYMBOLS -> SymbolLayouts.symbols
        Layer.MORE_SYMBOLS -> SymbolLayouts.moreSymbols
        Layer.NUMPAD -> NumpadLayout.numpad
    }

    /**
     * Rows of the tallest page, the letters page. The keyboard is as tall as
     * this many rows on every page; a page with fewer rows gets taller keys.
     */
    fun rowCount(numberRow: Boolean): Int = if (numberRow) LETTER_ROWS + 1 else LETTER_ROWS

    private fun qwerty(period: Key.Character, withNumberRow: Boolean) = KeyboardLayout(
        rows = buildList<List<Key>> {
            if (withNumberRow) add(numberRowKeys())
            add(letterKeys("qwertyuiop"))
            add(
                buildList<Key> {
                    add(Key.Spacer(HALF_KEY))
                    addAll(letterKeys("asdfghjkl"))
                    add(Key.Spacer(HALF_KEY))
                },
            )
            add(thirdRow(ControlAction.SHIFT, letterKeys("zxcvbnm")))
            add(bottomRow(ControlAction.SHOW_SYMBOLS, period))
        },
    )
}
