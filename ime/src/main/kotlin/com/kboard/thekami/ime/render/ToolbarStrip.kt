package com.kboard.thekami.ime.render

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.View
import com.kboard.thekami.ime.R
import kotlin.math.roundToInt

private const val MAX_ALPHA = 255f

private fun toolbarIcon(view: View, id: Int): Drawable =
    checkNotNull(view.context.getDrawable(id)) { "Missing drawable $id" }
        .mutate()
        .apply { setTint(view.context.getColor(R.color.kb_text)) }

/**
 * The toggle button at the left end of the strip, and the toolbar it opens.
 * The toolbar fades in over the suggestions; with animations turned off in the
 * system settings it appears at once. Cell 0 is the toggle, cells 1 and up are
 * the toolbar buttons, all [buttonWidth] wide.
 */
internal class ToolbarStrip(private val view: View, private val style: KeyboardStyle) {
    /** Suggestions are drawn to the right of this much space, which belongs to the toggle button. */
    val buttonWidth: Float = view.resources.getDimension(R.dimen.kb_toolbar_button_width)

    var isOpen = false
        private set

    private val top = (style.previewHeadroom - style.stripHeight).toFloat()
    private val bottom = style.previewHeadroom.toFloat()
    private val actions = ToolbarAction.entries
    private val actionIcons = actions.map { toolbarIcon(view, it.icon) }
    private val gridIcon = toolbarIcon(view, R.drawable.kb_ic_toolbar)
    private val closeIcon = toolbarIcon(view, R.drawable.kb_ic_close)
    private val coverPaint = Paint(style.backgroundPaint)
    private val cell = RectF()
    private var startedAtMs = 0L
    private var startProgress = 0f
    private var pointerDown = NO_POINTER
    private var cellDown = NO_CELL

    fun inBand(y: Float): Boolean = y >= top && y < bottom

    /** Returns true when the touch is on the toggle or, while open, on a toolbar button. */
    fun touchDown(pointerId: Int, x: Float, y: Float): Boolean {
        val touched = cellAt(x)
        val onButton = touched == TOGGLE_CELL || (isOpen && touched in FIRST_ACTION_CELL until firstFreeCell())
        val consumed = inBand(y) && onButton
        if (consumed) {
            pointerDown = pointerId
            cellDown = touched
            view.invalidate()
        }
        return consumed
    }

    /** The tapped toolbar button, or null. Tapping the toggle opens or closes the toolbar. */
    fun touchUp(pointerId: Int, x: Float): ToolbarAction? {
        if (pointerId != pointerDown) return null
        val tapped = cellDown.takeIf { it == cellAt(x) }
        cancelTouch()
        return when {
            tapped == TOGGLE_CELL -> {
                toggle()
                null
            }
            tapped != null -> actions[tapped - FIRST_ACTION_CELL]
            else -> null
        }
    }

    fun cancelTouch() {
        pointerDown = NO_POINTER
        cellDown = NO_CELL
        view.invalidate()
    }

    fun draw(canvas: Canvas, width: Float) {
        val now = SystemClock.uptimeMillis()
        val progress = progressAt(now)
        if (progress > 0f) drawPanel(canvas, width, progress)
        drawCell(canvas, if (isOpen) closeIcon else gridIcon, TOGGLE_CELL, MAX_ALPHA.roundToInt())
        if (now - startedAtMs < style.stripAnimationMs) view.postInvalidateOnAnimation()
    }

    private fun firstFreeCell(): Int = FIRST_ACTION_CELL + actions.size

    private fun cellAt(x: Float): Int = (x / buttonWidth).toInt()

    private fun toggle() {
        val now = SystemClock.uptimeMillis()
        startProgress = progressAt(now)
        isOpen = !isOpen
        startedAtMs = now
        // Without animations the toolbar starts at its final state.
        if (!ValueAnimator.areAnimatorsEnabled()) startProgress = if (isOpen) 1f else 0f
        view.invalidate()
    }

    /** 0 = closed, 1 = open, in between while fading. */
    private fun progressAt(now: Long): Float {
        val target = if (isOpen) 1f else 0f
        val fraction = ((now - startedAtMs).toFloat() / style.stripAnimationMs).coerceIn(0f, 1f)
        return startProgress + (target - startProgress) * fraction
    }

    private fun drawPanel(canvas: Canvas, width: Float, progress: Float) {
        val alpha = (progress * MAX_ALPHA).roundToInt()
        coverPaint.alpha = alpha
        canvas.drawRect(buttonWidth, top, width, bottom, coverPaint)
        actionIcons.forEachIndexed { index, icon -> drawCell(canvas, icon, FIRST_ACTION_CELL + index, alpha) }
    }

    private fun drawCell(canvas: Canvas, icon: Drawable, cellIndex: Int, alpha: Int) {
        cell.set(cellIndex * buttonWidth, top, (cellIndex + 1) * buttonWidth, bottom)
        if (cellIndex == cellDown) {
            canvas.drawRoundRect(cell, style.cornerRadius, style.cornerRadius, style.pressedPaint)
        }
        val half = style.iconHalfSize
        val centerX = cell.centerX().roundToInt()
        val centerY = cell.centerY().roundToInt()
        icon.alpha = alpha
        icon.setBounds(centerX - half, centerY - half, centerX + half, centerY + half)
        icon.draw(canvas)
    }

    private companion object {
        const val NO_POINTER = -1
        const val NO_CELL = -1
        const val TOGGLE_CELL = 0
        const val FIRST_ACTION_CELL = 1
    }
}
