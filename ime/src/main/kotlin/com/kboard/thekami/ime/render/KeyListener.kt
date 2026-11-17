package com.kboard.thekami.ime.render

import com.kboard.thekami.engine.layout.Key

/** Receives events from the [KeyboardView]. */
internal interface KeyListener {
    /** A finger went down on [key]. The space bar is reported when lifted if holding it does something. */
    fun onKeyPress(key: Key)

    /** [key] is still held and auto-repeating (backspace). */
    fun onKeyRepeat(key: Key)

    /** [key] was held long enough to count as a long press. Only the space bar reports this. */
    fun onKeyLongPress(key: Key)

    /** A finger lifted on suggestion number [index] (0 is the best one). */
    fun onSuggestionPick(index: Int)

    /** A toolbar button was tapped. */
    fun onToolbarAction(action: ToolbarAction)
}
