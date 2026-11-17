package com.kboard.thekami.ime.render

import com.kboard.thekami.ime.R

/** A button in the toolbar. They are shown in this order. [icon] is a drawable resource. */
internal enum class ToolbarAction(val icon: Int) {
    NUMPAD(R.drawable.kb_ic_numpad),
    SETTINGS(R.drawable.kb_ic_settings),
}
