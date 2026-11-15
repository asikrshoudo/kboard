package com.kboard.thekami.ime.service

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import com.kboard.thekami.data.settings.KeyboardSettings
import com.kboard.thekami.data.settings.createSettingsRepository
import com.kboard.thekami.engine.input.KeyInputHandler
import com.kboard.thekami.engine.layout.EnglishUsLayout
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.ime.feedback.KeyHaptics
import com.kboard.thekami.ime.input.EditorTraits
import com.kboard.thekami.ime.input.InputConnectionEditor
import com.kboard.thekami.ime.render.KeyListener
import com.kboard.thekami.ime.render.KeyboardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** The system-facing keyboard service. Wires the view to input, feedback and settings. */
class KboardImeService : InputMethodService() {
    private val inputHandler = KeyInputHandler(
        InputConnectionEditor(
            connection = { currentInputConnection },
            editorInfo = { currentInputEditorInfo },
        ),
    )
    private val haptics by lazy { KeyHaptics(this) }
    private val serviceScope = CoroutineScope(SupervisorJob())
    private var keyboardView: KeyboardView? = null

    // Written from a background coroutine, read on the main thread per keypress.
    @Volatile
    private var hapticsEnabled = KeyboardSettings().hapticsEnabled

    private val keyListener = object : KeyListener {
        override fun onKeyPress(key: Key) {
            if (hapticsEnabled) haptics.perform(key)
            typeKey(key)
        }

        override fun onKeyRepeat(key: Key) = typeKey(key)
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            createSettingsRepository(applicationContext).settings.collect {
                hapticsEnabled = it.hapticsEnabled
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onCreateInputView(): View =
        KeyboardView(this, EnglishUsLayout.layoutFor(inputHandler.layer), keyListener)
            .also { keyboardView = it }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        inputHandler.start(
            startLayer = EditorTraits.startLayer(info),
            autoCapitalize = EditorTraits.wantsSentenceCapitals(info),
        )
        showCurrentState()
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
        showCurrentState()
    }

    private fun showCurrentState() {
        keyboardView?.setLayout(EnglishUsLayout.layoutFor(inputHandler.layer))
        keyboardView?.setShiftActive(inputHandler.shifted)
    }
}
