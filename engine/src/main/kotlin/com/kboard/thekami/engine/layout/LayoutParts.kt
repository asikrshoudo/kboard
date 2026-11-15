package com.kboard.thekami.engine.layout

/** The weights of every row of every layout sum to this, so rows fill the same width. */
const val ROW_WEIGHT = 10f

internal const val HALF_KEY = 0.5f
internal const val WIDE_CONTROL = 1.5f
internal const val SPACE_BAR = 3.5f
internal const val ENTER_WEIGHT = 2f

internal val PERIOD = Key.Character(base = ".", shifted = ".")
private val COMMA = Key.Character(base = ",", shifted = ",")

/** One key per character of [chars]; shifted text is the same as the base text. */
internal fun characterKeys(chars: String): List<Key> =
    chars.map { Key.Character(base = it.toString(), shifted = it.toString()) }

/** One key per letter of [chars], typing the uppercase letter while shifted. */
internal fun letterKeys(chars: String): List<Key> =
    chars.map { Key.Character(base = it.toString(), shifted = it.uppercase()) }

/** Third row: [first] control, the [middle] keys, then backspace. */
internal fun thirdRow(first: ControlAction, middle: List<Key>): List<Key> = buildList {
    add(Key.Control(first, WIDE_CONTROL))
    addAll(middle)
    add(Key.Control(ControlAction.BACKSPACE, WIDE_CONTROL))
}

/** Bottom row shared by all pages; [switchTo] is the page-switch key, [period] the full stop. */
internal fun bottomRow(switchTo: ControlAction, period: Key.Character = PERIOD): List<Key> = listOf(
    Key.Control(switchTo, WIDE_CONTROL),
    Key.Control(ControlAction.SWITCH_LANGUAGE, DEFAULT_KEY_WEIGHT),
    COMMA,
    Key.Control(ControlAction.SPACE, SPACE_BAR),
    period,
    Key.Control(ControlAction.ENTER, ENTER_WEIGHT),
)
