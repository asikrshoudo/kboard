package com.kboard.thekami.ime.render

import android.view.View
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key

/** Repeats backspace while a finger holds it down. Other keys never repeat. */
internal class KeyRepeater(private val view: View, private val listener: KeyListener) {
    private var pointerId = NO_POINTER
    private var key: Key? = null

    private val task = object : Runnable {
        override fun run() {
            val held = key ?: return
            listener.onKeyRepeat(held)
            view.postDelayed(this, REPEAT_INTERVAL_MS)
        }
    }

    /** Starts repeating [key] if it is repeatable; [pointerId] is the finger holding it. */
    fun start(pointerId: Int, key: Key) {
        if (key !is Key.Control || key.action != ControlAction.BACKSPACE) return
        stop()
        this.pointerId = pointerId
        this.key = key
        view.postDelayed(task, REPEAT_DELAY_MS)
    }

    fun stopIfHeldBy(pointerId: Int) {
        if (pointerId == this.pointerId) stop()
    }

    fun stop() {
        view.removeCallbacks(task)
        key = null
        pointerId = NO_POINTER
    }

    private companion object {
        const val NO_POINTER = -1

        /** How long backspace must be held before it starts repeating. */
        const val REPEAT_DELAY_MS = 400L

        /** Time between repeated backspaces. */
        const val REPEAT_INTERVAL_MS = 45L
    }
}
