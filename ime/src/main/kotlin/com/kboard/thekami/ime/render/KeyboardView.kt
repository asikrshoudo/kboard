package com.kboard.thekami.ime.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import com.kboard.thekami.engine.layout.KeyboardLayout
import com.kboard.thekami.engine.layout.LayoutGeometry
import kotlin.math.roundToInt

/**
 * Draws a [KeyboardLayout] with a suggestion strip and a toolbar above it.
 * Touches are handled by [KeyboardTouch]. The area above the strip is
 * transparent and holds key previews.
 *
 * The keyboard is [rowSlots] rows tall on every page. A page with fewer rows
 * (the symbol pages) shares the same height, so its keys are taller and
 * switching pages never changes the keyboard height.
 */
// ViewConstructor: created in code only. ClickableViewAccessibility: touch
// exploration support is not implemented yet.
@SuppressLint("ViewConstructor", "ClickableViewAccessibility")
internal class KeyboardView(
    context: Context,
    initialLayout: KeyboardLayout,
    listener: KeyListener,
) : View(context) {
    private val style = KeyboardStyle(context)
    private val painter = KeyboardPainter(style)
    private val strip = SuggestionStrip(this, style)
    private val toolbar = ToolbarStrip(this, style)
    private val touch = KeyboardTouch(this, style, listener, strip, toolbar)
    private var layout = initialLayout
    private var rowSlots = initialLayout.rows.size
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

    /** Key height as a percentage of the default. */
    var heightPercent: Int = FULL_PERCENT
        set(value) {
            if (field == value) return
            field = value
            requestLayout()
        }

    /** Whether a bubble with the letter is shown above a pressed key. */
    var keyPreviewEnabled: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            painter.previewsEnabled = value
            invalidate()
        }

    /** Whether holding the space bar does something (then space is typed when lifted). */
    var spaceLongPressEnabled: Boolean
        get() = touch.spaceLongPressEnabled
        set(value) {
            touch.spaceLongPressEnabled = value
        }

    /** Height of the transparent area above the strip. The app below is not covered by it. */
    val overlayHeight: Int get() = style.previewHeadroom - style.stripHeight

    fun setSuggestions(texts: List<String>) = strip.setSuggestions(texts)

    fun setLayout(newLayout: KeyboardLayout, newRowSlots: Int) {
        val rowsChanged = newRowSlots != rowSlots
        if (newLayout === layout && !rowsChanged) return
        layout = newLayout
        rowSlots = newRowSlots
        touch.reset()
        if (rowsChanged) requestLayout()
        placeKeys()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val keysHeight = (style.keyHeight * heightPercent / FULL_PERCENT * rowSlots).roundToInt()
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), keysHeight + style.previewHeadroom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        contentWidth = w - 2 * style.edgePadding
        touch.reset()
        placeKeys()
    }

    override fun onDraw(canvas: Canvas) {
        val keysTop = style.previewHeadroom.toFloat()
        val fullWidth = width.toFloat()
        canvas.drawRect(0f, keysTop - style.stripHeight, fullWidth, height.toFloat(), style.backgroundPaint)
        // The suggestions sit to the right of the toolbar toggle button.
        val saved = canvas.save()
        canvas.translate(toolbar.buttonWidth, 0f)
        strip.draw(canvas, fullWidth - toolbar.buttonWidth)
        canvas.restoreToCount(saved)
        toolbar.draw(canvas, fullWidth)
        val savedKeys = canvas.save()
        canvas.translate(style.edgePadding, keysTop)
        painter.draw(canvas, touch.placedKeys, touch.held, shiftActive, contentWidth)
        canvas.restoreToCount(savedKeys)
    }

    override fun onDetachedFromWindow() {
        touch.reset()
        super.onDetachedFromWindow()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touch.onTouch(event)
        return true
    }

    /** The rows share all the height below the strip equally, whatever the page. */
    private fun placeKeys() {
        val contentHeight = (height - style.previewHeadroom).toFloat()
        touch.placedKeys = LayoutGeometry.place(layout, contentWidth, contentHeight)
    }

    private companion object {
        /** 100 percent is the default key height. */
        const val FULL_PERCENT = 100
    }
}
