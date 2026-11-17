package com.kboard.thekami.ui

/** Everything the user can change on the home screen. */
class HomeActions(
    val onHapticsChange: (Boolean) -> Unit,
    val onAutocorrectChange: (Boolean) -> Unit,
    val onIncognitoChange: (Boolean) -> Unit,
    val onResetLearning: () -> Unit,
    val layout: LayoutActions,
    val sound: SoundActions,
    val gestures: GestureActions,
)
