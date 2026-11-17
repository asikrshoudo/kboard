package com.kboard.thekami.data.settings

/** What holding the space bar does. [id] is what gets saved. */
enum class SpaceLongPress(val id: String) {
    SWITCH_LANGUAGE("switch_language"),
    KEYBOARD_PICKER("keyboard_picker"),
    NOTHING("nothing"),
    ;

    companion object {
        /** An unknown or missing id means the default, switching language. */
        fun fromId(id: String?): SpaceLongPress = entries.firstOrNull { it.id == id } ?: SWITCH_LANGUAGE
    }
}
