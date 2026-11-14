package com.kboard.thekami.ime.service

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import com.kboard.thekami.engine.input.KeyInputHandler
import com.kboard.thekami.engine.layout.EnglishUsLayout
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.ime.input.InputConnectionEditor
import com.kboard.thekami.ime.render.KeyboardView

/** The system-facing keyboard service. Wires the view to the input handler. */
class KboardImeService : InputMethodService() {
    private val inputHandler = KeyInputHandler(
        InputConnectionEditor(
            connection = { currentInputConnection },
            editorInfo = { currentInputEditorInfo },
        ),
    )
    private var keyboardView: KeyboardView? = null

    override fun onCreateInputView(): View =
        KeyboardView(this, EnglishUsLayout.layout, ::onKey).also { keyboardView = it }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        inputHandler.reset()
        keyboardView?.setShiftActive(false)
    }

    private fun onKey(key: Key) {
        inputHandler.onKey(key)
        keyboardView?.setShiftActive(inputHandler.shifted)
    }
}
