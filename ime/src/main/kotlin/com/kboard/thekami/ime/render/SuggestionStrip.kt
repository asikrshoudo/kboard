package com.kboard.thekami.ime.render

import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import android.view.View
import android.animation.ValueAnimator
import kotlin.math.roundToInt

/**
 * The row of three suggestions above the keys. The best suggestion sits in the
 * middle (bold), the second best on its left and the third on its right.
 *
 * Every slot animates on its own: only a slot whose text changed fades the old
 * text out and the new text in, in place, with a small upward movement.
 * Slots that change together start a few milliseconds apart. With animations
 * turned off in the system settings the change is instant.
 */
internal class SuggestionStrip(private val view: View, private val style: KeyboardStyle) {
    private val top = (style.previewHeadroom - style.stripHeight).toFloat()
    private val bottom = style.previewHeadroom.toFloat()
    private val slots = List(SLOT_COUNT) { Slot() }
    private var pointerDown = NO_POINTER
    private var rankDown = NO_RANK

    private class Slot {
        var text = ""
        var previous = ""
        var startMs = 0L
        var animating = false
    }

    /** [texts] are ordered best first. */
    fun setSuggestions(texts: List<String>) {
        val now = SystemClock.uptimeMillis()
        val animate = ValueAnimator.areAnimatorsEnabled()
        var changed = 0
        slots.forEachIndexed { index, slot ->
            val text = texts.getOrNull(RANK_IN_SLOT[index]).orEmpty()
            if (text != slot.text) {
                slot.previous = slot.text
                slot.text = text
                slot.animating = animate
                slot.startMs = now + changed * style.stripStaggerMs
                changed++
            }
        }
        view.invalidate()
    }

    fun draw(canvas: Canvas, width: Float) {
        val now = SystemClock.uptimeMillis()
        var running = false
        slots.forEachIndexed { index, slot ->
            val progress = progressOf(slot, now)
            running = running || slot.animating
            drawSlot(canvas, index, slot, progress, width)
        }
        if (running) view.postInvalidateOnAnimation()
    }

    /** Returns true when the touch is inside the strip, so the keys must not see it. */
    fun touchDown(pointerId: Int, x: Float, y: Float, width: Float): Boolean {
        if (y < top || y >= bottom) return false
        pointerDown = pointerId
        rankDown = rankAt(x, width)
        return true
    }

    /** The rank of the tapped suggestion, or null if this finger did not tap one. */
    fun touchUp(pointerId: Int, x: Float, width: Float): Int? {
        if (pointerId != pointerDown) return null
        pointerDown = NO_POINTER
        return rankDown.takeIf { it != NO_RANK && it == rankAt(x, width) }
    }

    fun cancelTouch() {
        pointerDown = NO_POINTER
    }

    private fun rankAt(x: Float, width: Float): Int {
        val slot = (x / (width / SLOT_COUNT)).toInt().coerceIn(0, SLOT_COUNT - 1)
        return if (slots[slot].text.isEmpty()) NO_RANK else RANK_IN_SLOT[slot]
    }

    /** 0 = the change just started, 1 = finished. Also ends the animation of the slot. */
    private fun progressOf(slot: Slot, now: Long): Float {
        if (!slot.animating) return 1f
        val progress = ((now - slot.startMs).toFloat() / style.stripAnimationMs).coerceIn(0f, 1f)
        if (progress >= 1f) slot.animating = false
        return progress
    }

    private fun drawSlot(canvas: Canvas, index: Int, slot: Slot, progress: Float, width: Float) {
        val slotWidth = width / SLOT_COUNT
        val left = index * slotWidth
        val centerX = left + slotWidth / 2f
        val baseline = (top + bottom) / 2f + style.stripBaselineOffset
        val paint = if (index == PRIMARY_SLOT) style.stripPrimaryPaint else style.stripPaint
        val saved = canvas.save()
        canvas.clipRect(left, top, left + slotWidth, bottom)
        if (progress < 1f) {
            val eased = 1f - (1f - progress) * (1f - progress)
            drawText(canvas, slot.previous, centerX, baseline - eased * style.stripRise, 1f - eased, paint)
            drawText(canvas, slot.text, centerX, baseline + (1f - eased) * style.stripRise, eased, paint)
        } else {
            drawText(canvas, slot.text, centerX, baseline, 1f, paint)
        }
        canvas.restoreToCount(saved)
    }

    private fun drawText(canvas: Canvas, text: String, x: Float, y: Float, alpha: Float, paint: Paint) {
        if (text.isEmpty() || alpha <= 0f) return
        paint.alpha = (alpha * MAX_ALPHA).roundToInt()
        canvas.drawText(text, x, y, paint)
    }

    private companion object {
        const val SLOT_COUNT = 3
        const val PRIMARY_SLOT = 1
        const val NO_POINTER = -1
        const val NO_RANK = -1
        const val MAX_ALPHA = 255f

        /** The suggestion rank shown in each slot: left shows rank 1, middle rank 0, right rank 2. */
        val RANK_IN_SLOT = intArrayOf(1, 0, 2)
    }
}
