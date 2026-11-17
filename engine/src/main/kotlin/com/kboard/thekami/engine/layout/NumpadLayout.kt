package com.kboard.thekami.engine.layout

/** A number pad with big keys, for number fields and for typing many digits. */
object NumpadLayout {
    /** Four equal columns fill a row. */
    private const val KEY_WEIGHT = ROW_WEIGHT / 4

    val numpad = KeyboardLayout(
        rows = listOf(
            listOf(digit('1'), digit('2'), digit('3'), control(ControlAction.BACKSPACE)),
            listOf(digit('4'), digit('5'), digit('6'), symbol("-")),
            listOf(digit('7'), digit('8'), digit('9'), symbol(".")),
            listOf(control(ControlAction.SHOW_LETTERS), digit('0'), symbol(","), control(ControlAction.ENTER)),
        ),
    )

    private fun digit(char: Char): Key = symbol(char.toString())

    private fun symbol(text: String): Key = Key.Character(base = text, shifted = text, weight = KEY_WEIGHT)

    private fun control(action: ControlAction): Key = Key.Control(action, KEY_WEIGHT)
}
