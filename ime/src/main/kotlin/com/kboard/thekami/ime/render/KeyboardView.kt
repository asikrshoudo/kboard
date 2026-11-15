package com.kboard.thekami.ime.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.KeyboardLayout
import com.kboard.thekami.engine.layout.LayoutGeometry
import com.kboard.thekami.engine.layout.PlacedKey
import kotlin.math.roundToInt

/**
 * Shows a [KeyboardLayout] and reports touches to a [KeyListener]. Keys fire
 * on touch-down for the lowest latency, and every finger is tracked so fast
 * two-thumb typing works. A transparent strip above the keys holds previews.
 */
// ViewConstructor: created in code only. ClickableViewAccessibility: touch
// exploration support is not implemented yet.
@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
internal class KeyboardView(
    context: Context,
    private val layout: KeyboardLayout,
    private val listener: KeyListener,
) : View(context) {
    private val style = KeyboardStyle(context)
    private val painter = KeyboardPainter(style)
    private val heldKeys = SparseArray<PlacedKey>()
    private var placedKeys: List<PlacedKey> = emptyList()
    private var contentWidth = 0f
    private var shiftActive = false
    private var repeatPointerId = NO_POINTER
    private var repeatKey: Key? = null

    private val repeatTask = object : Runnable {
        override fun run() {
            val key = repeatKey ?: return
            listener.onKeyRepeat(key)
            postDelayed(this, REPEAT_INTERVAL_MS)
        }
    }

    /** Height of the transparent strip above the keys, in pixels. */
    val previewHeadroom: Int get() = style.previewHeadroom

    fun setShiftActive(active: Boolean) {
        if (shiftActive == active) return
        shiftActive = active
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val keysHeight = (style.keyHeight * layout.rows.size).roundToInt()
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), keysHeight + style.previewHeadroom)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        contentWidth = width - 2 * style.edgePadding
        val contentHeight = (height - style.previewHeadroom).toFloat()
        placedKeys = LayoutGeometry.place(layout, contentWidth, contentHeight)
        heldKeys.clear()
        stopRepeat()
    }

    override fun onDraw(canvas: Canvas) {
        val top = style.previewHeadroom.toFloat()
        canvas.drawRect(0f, top, width.toFloat(), height.toFloat(), style.backgroundPaint)
        val saved = canvas.save()
        canvas.translate(style.edgePadding, top)
        painter.draw(canvas, placedKeys, heldKeys, shiftActive, contentWidth)
        canvas.restoreToCount(saved)
    }

    override fun onDetachedFromWindow() {
        stopRepeat()
        super.onDetachedFromWindow()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> press(event, event.actionIndex)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> release(event.getPointerId(event.actionIndex))
            MotionEvent.ACTION_CANCEL -> {
                heldKeys.clear()
                stopRepeat()
                invalidate()
            }
        }
        return true
    }

    private fun press(event: MotionEvent, index: Int) {
        val x = event.getX(index) - style.edgePadding
        val y = event.getY(index) - style.previewHeadroom
        val placed = LayoutGeometry.keyAt(placedKeys, x, y) ?: return
        val pointerId = event.getPointerId(index)
        heldKeys.put(pointerId, placed)
        invalidate()
        listener.onKeyPress(placed.key)
        if (isRepeatable(placed.key)) startRepeat(pointerId, placed.key)
    }

    private fun release(pointerId: Int) {
        heldKeys.remove(pointerId)
        if (pointerId == repeatPointerId) stopRepeat()
        invalidate()
    }

    private fun isRepeatable(key: Key): Boolean =
        key is Key.Control && key.action == ControlAction.BACKSPACE

    private fun startRepeat(pointerId: Int, key: Key) {
        stopRepeat()
        repeatPointerId = pointerId
        repeatKey = key
        postDelayed(repeatTask, REPEAT_DELAY_MS)
    }

    private fun stopRepeat() {
        removeCallbacks(repeatTask)
        repeatKey = null
        repeatPointerId = NO_POINTER
    }

    private companion object {
        const val NO_POINTER = -1

        /** How long backspace must be held before it starts repeating. */
        const val REPEAT_DELAY_MS = 400L

        /** Time between repeated backspaces. */
        const val REPEAT_INTERVAL_MS = 45L
    }
}
