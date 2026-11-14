package com.kboard.thekami.engine.layout

/** English (US) QWERTY layout. The weights of every row sum to [ROW_WEIGHT]. */
object EnglishUsLayout {
    const val ROW_WEIGHT = 10f

    private const val HALF_KEY = 0.5f
    private const val WIDE_CONTROL = 1.5f
    private const val SPACE_BAR = 6f
    private const val ENTER_WEIGHT = 2f

    val layout = KeyboardLayout(
        rows = listOf(
            letters("qwertyuiop"),
            buildList {
                add(Key.Spacer(HALF_KEY))
                addAll(letters("asdfghjkl"))
                add(Key.Spacer(HALF_KEY))
            },
            buildList {
                add(Key.Control(ControlAction.SHIFT, WIDE_CONTROL))
                addAll(letters("zxcvbnm"))
                add(Key.Control(ControlAction.BACKSPACE, WIDE_CONTROL))
            },
            listOf(
                punctuation(","),
                Key.Control(ControlAction.SPACE, SPACE_BAR),
                punctuation("."),
                Key.Control(ControlAction.ENTER, ENTER_WEIGHT),
            ),
        ),
    )

    private fun letters(chars: String): List<Key> =
        chars.map { Key.Character(base = it.toString(), shifted = it.uppercase()) }

    private fun punctuation(mark: String): Key = Key.Character(base = mark, shifted = mark)
}
