package com.kboard.thekami.data.settings

/**
 * User choices that the keyboard and the companion app share. [languageId] is
 * the id of the language the keyboard was last using; empty means the default.
 */
data class KeyboardSettings(
    val hapticsEnabled: Boolean = true,
    val languageId: String = "",
)
