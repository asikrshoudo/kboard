package com.kboard.thekami.engine.layout

/** Digit and symbol pages. Not tied to a language; includes the Taka sign. */
object SymbolLayouts {
    val symbols = KeyboardLayout(
        rows = listOf(
            characterKeys("1234567890"),
            characterKeys("@#\$_&-+()/"),
            thirdRow(ControlAction.SHOW_MORE_SYMBOLS, characterKeys("*\"':;!?")),
            bottomRow(ControlAction.SHOW_LETTERS),
        ),
    )

    val moreSymbols = KeyboardLayout(
        rows = listOf(
            characterKeys("`~|{}[]<>\\"),
            characterKeys("€£¥৳₹^°©®™"),
            thirdRow(ControlAction.SHOW_SYMBOLS, characterKeys("_=§¿¡…•")),
            bottomRow(ControlAction.SHOW_LETTERS),
        ),
    )
}
