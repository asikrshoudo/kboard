package com.kboard.thekami.ime.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.SparseArray
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.PlacedKey
import kotlin.math.roundToInt

/** Draws keys and key previews. Allocates nothing per frame. */
internal class KeyboardPainter(private val style: KeyboardStyle) {
    private val keyRect = RectF()
    private val previewRect = RectF()

    /** Draws in key coordinates: the origin is the top-left corner of the first key. */
    fun draw(
        canvas: Canvas,
        keys: List<PlacedKey>,
        held: SparseArray<PlacedKey>,
        shiftActive: Boolean,
        contentWidth: Float,
    ) {
        keys.forEach { drawKey(canvas, it, held, shiftActive) }
        for (i in 0 until held.size()) {
            drawPreview(canvas, held.valueAt(i), shiftActive, contentWidth)
        }
    }

    private fun drawKey(canvas: Canvas, placed: PlacedKey, held: SparseArray<PlacedKey>, shiftActive: Boolean) {
        val bounds = placed.bounds
        val inset = style.keyInset
        keyRect.set(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset)
        val paint = paintFor(placed, held, shiftActive)
        canvas.drawRoundRect(keyRect, style.cornerRadius, style.cornerRadius, paint)
        when (val key = placed.key) {
            is Key.Character -> canvas.drawText(
                textOf(key, shiftActive),
                keyRect.centerX(),
                keyRect.centerY() + style.textBaselineOffset,
                style.textPaint,
            )
            is Key.Control -> drawControl(canvas, key.action)
            is Key.Spacer -> Unit
        }
    }

    /** Page-switch keys show a text label; the others show an icon (space shows nothing). */
    private fun drawControl(canvas: Canvas, action: ControlAction) {
        val label = style.labelFor(action)
        if (label != null) {
            canvas.drawText(label, keyRect.centerX(), keyRect.centerY() + style.labelBaselineOffset, style.labelPaint)
        } else {
            drawIcon(canvas, action)
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

    /** A bubble with the typed character, shown above the pressed key. */
    private fun drawPreview(canvas: Canvas, placed: PlacedKey, shiftActive: Boolean, contentWidth: Float) {
        val key = placed.key as? Key.Character ?: return
        val bounds = placed.bounds
        val width = (bounds.right - bounds.left) * PREVIEW_WIDTH_SCALE
        val left = ((bounds.left + bounds.right) / 2f - width / 2f).coerceIn(0f, contentWidth - width)
        previewRect.set(left, bounds.top - style.previewHeight, left + width, bounds.top)
        canvas.drawRoundRect(previewRect, style.previewCornerRadius, style.previewCornerRadius, style.previewPaint)
        canvas.drawText(
            textOf(key, shiftActive),
            previewRect.centerX(),
            previewRect.centerY() + style.previewTextBaselineOffset,
            style.previewTextPaint,
        )
    }

    private fun paintFor(placed: PlacedKey, held: SparseArray<PlacedKey>, shiftActive: Boolean): Paint {
        val key = placed.key
        val shiftKeyOn = shiftActive && key is Key.Control && key.action == ControlAction.SHIFT
        return when {
            shiftKeyOn || isHeld(held, placed) -> style.pressedPaint
            key is Key.Control && key.action != ControlAction.SPACE -> style.controlPaint
            else -> style.keyPaint
        }
    }

    private fun isHeld(held: SparseArray<PlacedKey>, placed: PlacedKey): Boolean {
        for (i in 0 until held.size()) {
            if (held.valueAt(i) === placed) return true
        }
        return false
    }

    private fun textOf(key: Key.Character, shiftActive: Boolean): String =
        if (shiftActive) key.shifted else key.base

    private companion object {
        /** The preview bubble is this much wider than the key under the finger. */
        const val PREVIEW_WIDTH_SCALE = 1.4f
    }
}
