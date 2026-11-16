package com.kboard.thekami.ui

/** What the user can change in the layout card. */
class LayoutActions(
    val onNumberRowChange: (Boolean) -> Unit,
    val onKeyPreviewChange: (Boolean) -> Unit,
    val onHeightChange: (Int) -> Unit,
)
