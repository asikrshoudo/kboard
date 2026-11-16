package com.kboard.thekami.data.settings

/** How the keyboard looks and is laid out. */
data class KeyboardAppearance(
    val numberRowEnabled: Boolean = false,
    val heightPercent: Int = DEFAULT_HEIGHT_PERCENT,
    val keyPreviewEnabled: Boolean = true,
) {
    companion object {
        const val MIN_HEIGHT_PERCENT = 80
        const val MAX_HEIGHT_PERCENT = 140
        const val HEIGHT_STEP_PERCENT = 5
        const val DEFAULT_HEIGHT_PERCENT = 100
    }
}
