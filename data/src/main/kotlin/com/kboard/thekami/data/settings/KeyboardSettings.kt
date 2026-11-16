package com.kboard.thekami.data.settings

/**
 * User choices that the keyboard and the companion app share. [languageId] is
 * the id of the language the keyboard was last using; empty means the default.
 * While [incognitoEnabled] is on, nothing new is learned. [ignoredCorrections]
 * are the words the user undid an autocorrection for.
 */
data class KeyboardSettings(
    val hapticsEnabled: Boolean = true,
    val languageId: String = "",
    val incognitoEnabled: Boolean = false,
    val autocorrectEnabled: Boolean = true,
    val ignoredCorrections: Set<String> = emptySet(),
    val appearance: KeyboardAppearance = KeyboardAppearance(),
    val sound: KeyboardSound = KeyboardSound(),
)
