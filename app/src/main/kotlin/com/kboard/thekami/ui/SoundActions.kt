package com.kboard.thekami.ui

/** What the user can change in the sound card. */
class SoundActions(
    val onEnabledChange: (Boolean) -> Unit,
    val onVolumeChange: (Int) -> Unit,
)
