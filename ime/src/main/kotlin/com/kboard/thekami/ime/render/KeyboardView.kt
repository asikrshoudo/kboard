package com.kboard.thekami.ime.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import com.kboard.thekami.engine.layout.KeyboardLayout
import com.kboard.thekami.engine.layout.LayoutGeometry
import com.kboard.thekami.engine.layout.PlacedKey
import kotlin.math.roundToInt

/**
 * Shows a [KeyboardLayout] and reports touches to a [KeyListener]. Keys fire
 * on touch-down for the lowest latency, and every finger is tracked so fast
 * two-thumb typing works. A transparent strip above the keys holds previews.
 * All pages have the same number of rows, so switching pages never changes
 * the keyboard height.
 */
// ViewConstructor: created in code only. ClickableViewAccessibility: touch
// exploration support is not implemented yet.
@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
internal class KeyboardView(
    context: Context,
    initialLayout: KeyboardLayout,
    private val listener: KeyListener,
) : View(context) {
    private val style = KeyboardStyle(context)
    private val painter = KeyboardPainter(style)
    private val repeater = KeyRepeater(this, listener)
    private val heldKeys = SparseArray<PlacedKey>()
    private var layout = initialLayout
    private var placedKeys: List<PlacedKey> = emptyList()
    private var contentWidth = 0f
    private var shiftActive = false

    /** Height of the transparent strip above the keys, in pixels. */
    val previewHeadroom: Int get() = style.previewHeadroom

    fun setShiftActive(active: Boolean) {
        if (shiftActive == active) return
        shiftActive = active
        invalidate()
    }

    fun setSpaceLabel(label: String) {
        if (painter.spaceLabel == label) return
        painter.spaceLabel = label
        invalidate()
    }

    fun setLayout(newLayout: KeyboardLayout) {
        if (newLayout === layout) return
        layout = newLayout
        heldKeys.clear()
        repeater.stop()
        placeKeys()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val keysHeight = (style.keyHeight * layout.rows.size).roundToInt()
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), keysHeight + style.previewHeadroom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        contentWidth = w - 2 * style.edgePadding
        heldKeys.clear()
        repeater.stop()
        placeKeys()
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
        repeater.stop()
        super.onDetachedFromWindow()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> press(event, event.actionIndex)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> release(event.getPointerId(event.actionIndex))
            MotionEvent.ACTION_CANCEL -> {
                heldKeys.clear()
                repeater.stop()
                invalidate()
            }
        }
        return true
    }

    private fun placeKeys() {
        val contentHeight = (height - style.previewHeadroom).toFloat()
        placedKeys = LayoutGeometry.place(layout, contentWidth, contentHeight)
    }

    private fun press(event: MotionEvent, index: Int) {
        val x = event.getX(index) - style.edgePadding
        val y = event.getY(index) - style.previewHeadroom
        val placed = LayoutGeometry.keyAt(placedKeys, x, y) ?: return
        val pointerId = event.getPointerId(index)
        heldKeys.put(pointerId, placed)
        invalidate()
        listener.onKeyPress(placed.key)
        repeater.start(pointerId, placed.key)
    }

    private fun release(pointerId: Int) {
        heldKeys.remove(pointerId)
        repeater.stopIfHeldBy(pointerId)
        invalidate()
    }
}
