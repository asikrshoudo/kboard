package com.kboard.thekami.ime.render

import android.view.View
import android.view.ViewConfiguration
import com.kboard.thekami.engine.layout.Key

/** Reports a key that one finger has held for the system's long-press time. */
internal class KeyLongPress(private val view: View, private val onLongPress: (Key) -> Unit) {
    private var pointerId = NO_POINTER
    private var key: Key? = null
    private var fired = false

    private val task = Runnable {
        key?.let {
            fired = true
            onLongPress(it)
        }
    }

    fun start(pointerId: Int, key: Key) {
        cancel()
        this.pointerId = pointerId
        this.key = key
        view.postDelayed(task, ViewConfiguration.getLongPressTimeout().toLong())
    }

    /** Ends the press of [pointerId]. Returns true if it had already become a long press. */
    fun finish(pointerId: Int): Boolean {
        val wasLongPress = pointerId == this.pointerId && fired
        if (pointerId == this.pointerId) cancel()
        return wasLongPress
    }

    fun cancel() {
        view.removeCallbacks(task)
        pointerId = NO_POINTER
        key = null
        fired = false
    }

    private companion object {
        const val NO_POINTER = -1
    }
}
