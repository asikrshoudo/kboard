package com.kboard.thekami.ui

import com.kboard.thekami.data.settings.SpaceLongPress

/** What the user can change in the gestures card. */
class GestureActions(
    val onSpaceLongPressChange: (SpaceLongPress) -> Unit,
)
