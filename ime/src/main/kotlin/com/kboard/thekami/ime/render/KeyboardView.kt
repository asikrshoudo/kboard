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
 * Shows a [KeyboardLayout] with a suggestion strip above it and reports
 * touches to a [KeyListener]. Keys fire on touch-down for the lowest latency,
 * and every finger is tracked so fast two-thumb typing works. The area above
 * the strip is transparent and holds key previews. All pages have the same
 * number of rows, so switching pages never changes the keyboard height.
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
    private val strip = SuggestionStrip(this, style)
    private val repeater = KeyRepeater(this, listener)
    private val heldKeys = SparseArray<PlacedKey>()
    private var layout = initialLayout
    private var placedKeys: List<PlacedKey> = emptyList()
    private var contentWidth = 0f

    var shiftActive: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            invalidate()
        }

    /** Text shown on the space bar, normally the name of the current language. */
    var spaceLabel: String = ""
        set(value) {
            if (field == value) return
            field = value
            painter.spaceLabel = value
            invalidate()
        }

    /** Height of the transparent area above the strip. The app below is not covered by it. */
    val overlayHeight: Int get() = style.previewHeadroom - style.stripHeight

    fun setSuggestions(texts: List<String>) = strip.setSuggestions(texts)

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
        val keysTop = style.previewHeadroom.toFloat()
        canvas.drawRect(0f, keysTop - style.stripHeight, width.toFloat(), height.toFloat(), style.backgroundPaint)
        strip.draw(canvas, width.toFloat())
        val saved = canvas.save()
        canvas.translate(style.edgePadding, keysTop)
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
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> release(event, event.actionIndex)
            MotionEvent.ACTION_CANCEL -> {
                heldKeys.clear()
                repeater.stop()
                strip.cancelTouch()
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
        val x = event.getX(index)
        val y = event.getY(index)
        val pointerId = event.getPointerId(index)
        if (strip.touchDown(pointerId, x, y, width.toFloat())) return
        val placed = LayoutGeometry.keyAt(placedKeys, x - style.edgePadding, y - style.previewHeadroom) ?: return
        heldKeys.put(pointerId, placed)
        invalidate()
        listener.onKeyPress(placed.key)
        repeater.start(pointerId, placed.key)
    }

    private fun release(event: MotionEvent, index: Int) {
        val pointerId = event.getPointerId(index)
        strip.touchUp(pointerId, event.getX(index), width.toFloat())?.let { listener.onSuggestionPick(it) }
        heldKeys.remove(pointerId)
        repeater.stopIfHeldBy(pointerId)
        invalidate()
    }
}
