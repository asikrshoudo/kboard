package com.kboard.thekami.ime.render

import com.kboard.thekami.engine.layout.Key

/** Receives key events from the [KeyboardView]. */
internal interface KeyListener {
    /** A finger went down on [key]. */
    fun onKeyPress(key: Key)

    /** [key] is still held and auto-repeating (backspace). */
    fun onKeyRepeat(key: Key)
}
