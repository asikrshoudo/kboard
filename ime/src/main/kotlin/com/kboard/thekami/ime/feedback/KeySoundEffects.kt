package com.kboard.thekami.ime.feedback

import android.media.AudioManager
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key

/** Which of the phone's own key click sounds goes with which key. */
internal object KeySoundEffects {
    /** Choosing a suggestion sounds like a normal key. */
    const val SUGGESTION_EFFECT = AudioManager.FX_KEYPRESS_STANDARD

    fun effectFor(key: Key): Int? = when (key) {
        is Key.Character -> AudioManager.FX_KEYPRESS_STANDARD
        is Key.Control -> effectFor(key.action)
        is Key.Spacer -> null
    }

    private fun effectFor(action: ControlAction): Int = when (action) {
        ControlAction.SPACE -> AudioManager.FX_KEYPRESS_SPACEBAR
        ControlAction.BACKSPACE -> AudioManager.FX_KEYPRESS_DELETE
        ControlAction.ENTER -> AudioManager.FX_KEYPRESS_RETURN
        ControlAction.SHIFT,
        ControlAction.SWITCH_LANGUAGE,
        ControlAction.SHOW_LETTERS,
        ControlAction.SHOW_SYMBOLS,
        ControlAction.SHOW_MORE_SYMBOLS,
        -> AudioManager.FX_KEYPRESS_STANDARD
    }
}
