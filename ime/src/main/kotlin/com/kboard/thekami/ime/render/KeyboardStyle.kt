package com.kboard.thekami.ime.render

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.ime.R
import kotlin.math.roundToInt

/** Dimensions, colors, paints and icons for drawing the keyboard, resolved once. */
internal class KeyboardStyle(context: Context) {
    private val resources = context.resources

    val keyHeight: Float = resources.getDimension(R.dimen.kb_key_height)

    /** Space kept free on each side of a key; two insets make the gap between keys. */
    val keyInset: Float = resources.getDimension(R.dimen.kb_key_gap) / 2f
    val cornerRadius: Float = resources.getDimension(R.dimen.kb_key_corner_radius)
    val iconHalfSize: Int = (resources.getDimension(R.dimen.kb_icon_size) / 2f).roundToInt()
    val backgroundColor: Int = context.getColor(R.color.kb_background)
    val keyPaint: Paint = fill(context.getColor(R.color.kb_key))
    val controlPaint: Paint = fill(context.getColor(R.color.kb_control_key))
    val pressedPaint: Paint = fill(context.getColor(R.color.kb_key_pressed))

    private val textColor: Int = context.getColor(R.color.kb_text)
    val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = resources.getDimension(R.dimen.kb_text_size)
        textAlign = Paint.Align.CENTER
    }

    /** Offset from a key's vertical center to the baseline that centers the text. */
    val textBaselineOffset: Float = with(textPaint.fontMetrics) { -(ascent + descent) / 2f }

    private val icons: Map<ControlAction, Drawable> = mapOf(
        ControlAction.SHIFT to icon(context, R.drawable.kb_ic_shift),
        ControlAction.BACKSPACE to icon(context, R.drawable.kb_ic_backspace),
        ControlAction.ENTER to icon(context, R.drawable.kb_ic_enter),
    )

    fun iconFor(action: ControlAction): Drawable? = icons[action]

    private fun fill(color: Int): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }

    private fun icon(context: Context, id: Int): Drawable =
        checkNotNull(context.getDrawable(id)) { "Missing drawable $id" }
            .mutate()
            .apply { setTint(textColor) }
}
