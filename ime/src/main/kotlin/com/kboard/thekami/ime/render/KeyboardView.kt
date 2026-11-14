package com.kboard.thekami.ime.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
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
 * Draws a [KeyboardLayout] with Canvas and reports key presses. Keys are
 * typed on touch-down for the lowest latency, and every finger is tracked so
 * fast two-thumb typing works.
 */
// ViewConstructor: created in code only. ClickableViewAccessibility: touch
// exploration support is not implemented yet.
@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
internal class KeyboardView(
    context: Context,
    private val layout: KeyboardLayout,
    private val onKey: (Key) -> Unit,
) : View(context) {
    private val style = KeyboardStyle(context)
    private val keyRect = RectF()
    private val heldKeys = SparseArray<PlacedKey>()
    private var placedKeys: List<PlacedKey> = emptyList()
    private var shiftActive = false

    fun setShiftActive(active: Boolean) {
        if (shiftActive == active) return
        shiftActive = active
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = (style.keyHeight * layout.rows.size).roundToInt()
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        placedKeys = LayoutGeometry.place(layout, width.toFloat(), height.toFloat())
        heldKeys.clear()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(style.backgroundColor)
        placedKeys.forEach { drawKey(canvas, it) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> press(event, event.actionIndex)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> release(event.getPointerId(event.actionIndex))
            MotionEvent.ACTION_CANCEL -> {
                heldKeys.clear()
                invalidate()
            }
        }
        return true
    }

    private fun press(event: MotionEvent, index: Int) {
        val placed = LayoutGeometry.keyAt(placedKeys, event.getX(index), event.getY(index)) ?: return
        heldKeys.put(event.getPointerId(index), placed)
        invalidate()
        onKey(placed.key)
    }

    private fun release(pointerId: Int) {
        heldKeys.remove(pointerId)
        invalidate()
    }

    private fun drawKey(canvas: Canvas, placed: PlacedKey) {
        val bounds = placed.bounds
        val inset = style.keyInset
        keyRect.set(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset)
        canvas.drawRoundRect(keyRect, style.cornerRadius, style.cornerRadius, paintFor(placed))
        when (val key = placed.key) {
            is Key.Character -> canvas.drawText(
                if (shiftActive) key.shifted else key.base,
                keyRect.centerX(),
                keyRect.centerY() + style.textBaselineOffset,
                style.textPaint,
            )
            is Key.Control -> drawIcon(canvas, key.action)
            is Key.Spacer -> Unit
        }
    }

    private fun drawIcon(canvas: Canvas, action: ControlAction) {
        val icon = style.iconFor(action) ?: return
        val half = style.iconHalfSize
        val centerX = keyRect.centerX().roundToInt()
        val centerY = keyRect.centerY().roundToInt()
        icon.setBounds(centerX - half, centerY - half, centerX + half, centerY + half)
        icon.draw(canvas)
    }

    private fun paintFor(placed: PlacedKey): Paint {
        val key = placed.key
        val shiftKeyOn = shiftActive && key is Key.Control && key.action == ControlAction.SHIFT
        return when {
            shiftKeyOn || isHeld(placed) -> style.pressedPaint
            key is Key.Control && key.action != ControlAction.SPACE -> style.controlPaint
            else -> style.keyPaint
        }
    }

    private fun isHeld(placed: PlacedKey): Boolean {
        for (i in 0 until heldKeys.size()) {
            if (heldKeys.valueAt(i) === placed) return true
        }
        return false
    }
}
