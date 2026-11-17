package com.kboard.thekami.ime.render

import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.LayoutGeometry
import com.kboard.thekami.engine.layout.PlacedKey

/**
 * Turns touches on the keyboard into key, suggestion and toolbar events. Keys
 * fire on touch-down for the lowest latency, and every finger is tracked so
 * fast two-thumb typing works. The one exception is the space bar while holding
 * it does something: it is typed when the finger lifts, because only then is it
 * known that this was not a long press.
 */
internal class KeyboardTouch(
    private val view: View,
    private val style: KeyboardStyle,
    private val listener: KeyListener,
    private val strip: SuggestionStrip,
    private val toolbar: ToolbarStrip,
) {
    /** The keys on screen, in key coordinates. Set by the view. */
    var placedKeys: List<PlacedKey> = emptyList()

    /** The keys that are held down, for drawing them pressed. */
    val held = SparseArray<PlacedKey>()

    /** True when holding the space bar does something. */
    var spaceLongPressEnabled = false

    private val repeater = KeyRepeater(view, listener)
    private val longPress = KeyLongPress(view) { listener.onKeyLongPress(it) }
    private var deferredSpacePointer = NO_POINTER
    private var deferredSpace: Key? = null

    fun onTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> down(event, event.actionIndex)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> up(event, event.actionIndex)
            MotionEvent.ACTION_CANCEL -> reset()
        }
    }

    /** Forgets every finger and timer, for when the keys change or the view goes away. */
    fun reset() {
        held.clear()
        repeater.stop()
        longPress.cancel()
        deferredSpacePointer = NO_POINTER
        deferredSpace = null
        strip.cancelTouch()
        toolbar.cancelTouch()
    }

    private fun down(event: MotionEvent, index: Int) {
        val x = event.getX(index)
        val y = event.getY(index)
        val pointerId = event.getPointerId(index)
        if (toolbar.inBand(y)) {
            val offset = toolbar.buttonWidth
            if (!toolbar.touchDown(pointerId, x, y) && !toolbar.isOpen) {
                strip.touchDown(pointerId, x - offset, y, view.width - offset)
            }
            return
        }
        val placed = LayoutGeometry.keyAt(placedKeys, x - style.edgePadding, y - style.previewHeadroom) ?: return
        held.put(pointerId, placed)
        view.invalidate()
        pressKey(pointerId, placed.key)
    }

    private fun pressKey(pointerId: Int, key: Key) {
        val isSpace = key is Key.Control && key.action == ControlAction.SPACE
        if (spaceLongPressEnabled && isSpace) {
            deferredSpacePointer = pointerId
            deferredSpace = key
            longPress.start(pointerId, key)
        } else {
            listener.onKeyPress(key)
            repeater.start(pointerId, key)
        }
    }

    private fun up(event: MotionEvent, index: Int) {
        val pointerId = event.getPointerId(index)
        val x = event.getX(index)
        val offset = toolbar.buttonWidth
        toolbar.touchUp(pointerId, x)?.let { listener.onToolbarAction(it) }
        strip.touchUp(pointerId, x - offset, view.width - offset)?.let { listener.onSuggestionPick(it) }
        releaseKey(pointerId)
        held.remove(pointerId)
        view.invalidate()
    }

    private fun releaseKey(pointerId: Int) {
        repeater.stopIfHeldBy(pointerId)
        val wasLongPress = longPress.finish(pointerId)
        if (pointerId == deferredSpacePointer) {
            deferredSpacePointer = NO_POINTER
            if (!wasLongPress) deferredSpace?.let { listener.onKeyPress(it) }
            deferredSpace = null
        }
    }

    private companion object {
        const val NO_POINTER = -1
    }
}
