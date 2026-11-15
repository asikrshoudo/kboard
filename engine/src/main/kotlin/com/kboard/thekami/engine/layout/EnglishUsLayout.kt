package com.kboard.thekami.engine.layout

/** English (US) QWERTY letters page, plus access to the shared symbol pages. */
object EnglishUsLayout {
    val letters = KeyboardLayout(
        rows = listOf(
            letterKeys("qwertyuiop"),
            buildList {
                add(Key.Spacer(HALF_KEY))
                addAll(letterKeys("asdfghjkl"))
                add(Key.Spacer(HALF_KEY))
            },
            thirdRow(ControlAction.SHIFT, letterKeys("zxcvbnm")),
            bottomRow(ControlAction.SHOW_SYMBOLS),
        ),
    )

    fun layoutFor(layer: Layer): KeyboardLayout = when (layer) {
        Layer.LETTERS -> letters
        Layer.SYMBOLS -> SymbolLayouts.symbols
        Layer.MORE_SYMBOLS -> SymbolLayouts.moreSymbols
    }
}
