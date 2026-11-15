package com.kboard.thekami.engine.layout

/** Default relative width of a key. Keys share their row's width by weight. */
const val DEFAULT_KEY_WEIGHT = 1f

/** Actions of keys that do not type a fixed character. */
enum class ControlAction {
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    SHOW_LETTERS,
    SHOW_SYMBOLS,
    SHOW_MORE_SYMBOLS,
}

sealed interface Key {
    /** Relative width of this key within its row. */
    val weight: Float

    /** Types [base], or [shifted] while shift is active. */
    data class Character(
        val base: String,
        val shifted: String,
        override val weight: Float = DEFAULT_KEY_WEIGHT,
    ) : Key

    /** Performs [action] instead of typing text. */
    data class Control(val action: ControlAction, override val weight: Float) : Key

    /** Empty space in a row, used to center short rows. Never drawn or pressed. */
    data class Spacer(override val weight: Float) : Key
}
