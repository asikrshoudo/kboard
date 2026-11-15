package com.kboard.thekami.ime.render

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.roundToInt

/**
 * The row of suggestions above the keys. When the suggestions change, the old
 * ones fade out while sliding slightly left and the new ones fade in from the
 * right. With animations turned off in the system settings the change is
 * instant. Tapping a suggestion is reported when the finger lifts.
 */
internal class SuggestionStrip(private val view: View, private val style: KeyboardStyle) {
    private val top = (style.previewHeadroom - style.stripHeight).toFloat()
    private val bottom = style.previewHeadroom.toFloat()
    private var current: List<String> = emptyList()
    private var previous: List<String> = emptyList()
    private var progress = 1f
    private var downPointer = NO_POINTER
    private var downSlot: Int? = null

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = style.stripAnimationMs
        interpolator = DecelerateInterpolator()
        addUpdateListener {
            progress = it.animatedValue as Float
            view.invalidate()
        }
    }

    fun setSuggestions(texts: List<String>) {
        if (texts == current) return
        previous = current
        current = texts
        animator.cancel()
        if (ValueAnimator.areAnimatorsEnabled()) {
            progress = 0f
            animator.start()
        } else {
            progress = 1f
            view.invalidate()
        }
    }

    fun draw(canvas: Canvas, width: Float) {
        drawTexts(canvas, previous, width, alpha = 1f - progress, shift = -progress * style.stripShift)
        drawTexts(canvas, current, width, alpha = progress, shift = (1f - progress) * style.stripShift)
    }

    /** Returns true when the touch is inside the strip, so the keys must not see it. */
    fun touchDown(pointerId: Int, x: Float, y: Float, width: Float): Boolean {
        if (y < top || y >= bottom) return false
        downPointer = pointerId
        downSlot = slotAt(x, width)
        return true
    }

    /** The index of the tapped suggestion, or null if this finger did not tap one. */
    fun touchUp(pointerId: Int, x: Float, width: Float): Int? {
        if (pointerId != downPointer) return null
        downPointer = NO_POINTER
        return slotAt(x, width)?.takeIf { it == downSlot }
    }

    fun cancelTouch() {
        downPointer = NO_POINTER
    }

    private fun slotAt(x: Float, width: Float): Int? {
        val slot = (x / (width / SLOT_COUNT)).toInt()
        return slot.takeIf { it in 0 until minOf(current.size, SLOT_COUNT) }
    }

    private fun drawTexts(canvas: Canvas, texts: List<String>, width: Float, alpha: Float, shift: Float) {
        if (alpha <= 0f) return
        val slotWidth = width / SLOT_COUNT
        val paintAlpha = (alpha * MAX_ALPHA).roundToInt()
        for (index in 0 until minOf(texts.size, SLOT_COUNT)) {
            val paint = if (index == 0) style.stripPrimaryPaint else style.stripPaint
            paint.alpha = paintAlpha
            val left = index * slotWidth
            val saved = canvas.save()
            canvas.clipRect(left, top, left + slotWidth, bottom)
            canvas.drawText(
                texts[index],
                left + slotWidth / 2f + shift,
                (top + bottom) / 2f + style.stripBaselineOffset,
                paint,
            )
            canvas.restoreToCount(saved)
        }
    }

    private companion object {
        const val SLOT_COUNT = 3
        const val NO_POINTER = -1
        const val MAX_ALPHA = 255f
    }
}
