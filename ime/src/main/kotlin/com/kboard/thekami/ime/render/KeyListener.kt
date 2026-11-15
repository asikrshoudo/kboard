package com.kboard.thekami.ime.render

import com.kboard.thekami.engine.layout.Key

/** Receives events from the [KeyboardView]. */
internal interface KeyListener {
    /** A finger went down on [key]. */
    fun onKeyPress(key: Key)

    /** [key] is still held and auto-repeating (backspace). */
    fun onKeyRepeat(key: Key)

    /** A finger lifted on suggestion number [index] (0 is the leftmost). */
    fun onSuggestionPick(index: Int)
}
