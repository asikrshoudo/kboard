package com.kboard.thekami.ime.service

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import com.kboard.thekami.engine.input.KeyInputHandler
import com.kboard.thekami.engine.layout.EnglishUsLayout
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.ime.feedback.KeyHaptics
import com.kboard.thekami.ime.input.InputConnectionEditor
import com.kboard.thekami.ime.render.KeyListener
import com.kboard.thekami.ime.render.KeyboardView

/** The system-facing keyboard service. Wires the view to input and feedback. */
class KboardImeService : InputMethodService() {
    private val inputHandler = KeyInputHandler(
        InputConnectionEditor(
            connection = { currentInputConnection },
            editorInfo = { currentInputEditorInfo },
        ),
    )
    private val haptics by lazy { KeyHaptics(this) }
    private var keyboardView: KeyboardView? = null

    private val keyListener = object : KeyListener {
        override fun onKeyPress(key: Key) {
            haptics.perform(key)
            typeKey(key)
        }

        override fun onKeyRepeat(key: Key) = typeKey(key)
    }

    override fun onCreateInputView(): View =
        KeyboardView(this, EnglishUsLayout.layout, keyListener).also { keyboardView = it }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        inputHandler.reset()
        keyboardView?.setShiftActive(false)
    }

    /**
     * The transparent preview strip floats over the app instead of pushing it
     * up, and touches in that strip reach the app underneath.
     */
    override fun onComputeInsets(outInsets: Insets) {
        super.onComputeInsets(outInsets)
        val headroom = keyboardView?.previewHeadroom ?: return
        outInsets.contentTopInsets += headroom
        outInsets.visibleTopInsets += headroom
    }

    private fun typeKey(key: Key) {
        inputHandler.onKey(key)
        keyboardView?.setShiftActive(inputHandler.shifted)
    }
}
