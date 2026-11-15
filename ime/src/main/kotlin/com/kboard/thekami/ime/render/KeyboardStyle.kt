package com.kboard.thekami.ime.render

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.ime.R
import kotlin.math.roundToInt

/** Dimensions, colors, paints, labels and icons for drawing the keyboard, resolved once. */
internal class KeyboardStyle(context: Context) {
    private val resources = context.resources

    val keyHeight: Float = resources.getDimension(R.dimen.kb_key_height)

    /** Space kept free on each side of a key; two insets make the gap between keys. */
    val keyInset: Float = resources.getDimension(R.dimen.kb_key_gap) / 2f
    val edgePadding: Float = resources.getDimension(R.dimen.kb_edge_padding)
    val cornerRadius: Float = resources.getDimension(R.dimen.kb_key_corner_radius)
    val iconHalfSize: Int = (resources.getDimension(R.dimen.kb_icon_size) / 2f).roundToInt()

    /** Height of the transparent strip above the keys, in pixels. */
    val previewHeadroom: Int = resources.getDimensionPixelSize(R.dimen.kb_preview_headroom)
    val previewHeight: Float = resources.getDimension(R.dimen.kb_preview_height)
    val previewCornerRadius: Float = resources.getDimension(R.dimen.kb_preview_corner_radius)

    val backgroundPaint: Paint = fill(context.getColor(R.color.kb_background))
    val keyPaint: Paint = fill(context.getColor(R.color.kb_key))
    val controlPaint: Paint = fill(context.getColor(R.color.kb_control_key))
    val pressedPaint: Paint = fill(context.getColor(R.color.kb_key_pressed))
    val previewPaint: Paint = fill(context.getColor(R.color.kb_preview))

    private val textColor: Int = context.getColor(R.color.kb_text)
    val textPaint: Paint = textPaint(R.dimen.kb_text_size)
    val previewTextPaint: Paint = textPaint(R.dimen.kb_preview_text_size)
    val labelPaint: Paint = textPaint(R.dimen.kb_label_text_size)
    val textBaselineOffset: Float = baselineOffset(textPaint)
    val previewTextBaselineOffset: Float = baselineOffset(previewTextPaint)
    val labelBaselineOffset: Float = baselineOffset(labelPaint)

    private val icons: Map<ControlAction, Drawable> = mapOf(
        ControlAction.SHIFT to icon(context, R.drawable.kb_ic_shift),
        ControlAction.BACKSPACE to icon(context, R.drawable.kb_ic_backspace),
        ControlAction.ENTER to icon(context, R.drawable.kb_ic_enter),
    )

    private val labels: Map<ControlAction, String> = mapOf(
        ControlAction.SHOW_LETTERS to context.getString(R.string.kb_label_letters),
        ControlAction.SHOW_SYMBOLS to context.getString(R.string.kb_label_symbols),
        ControlAction.SHOW_MORE_SYMBOLS to context.getString(R.string.kb_label_more_symbols),
    )

    fun iconFor(action: ControlAction): Drawable? = icons[action]

    fun labelFor(action: ControlAction): String? = labels[action]

    private fun fill(color: Int): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }

    private fun textPaint(sizeRes: Int): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = resources.getDimension(sizeRes)
        textAlign = Paint.Align.CENTER
    }

    /** Offset from a line's vertical center to the baseline that centers the text. */
    private fun baselineOffset(paint: Paint): Float = with(paint.fontMetrics) { -(ascent + descent) / 2f }

    private fun icon(context: Context, id: Int): Drawable =
        checkNotNull(context.getDrawable(id)) { "Missing drawable $id" }
            .mutate()
            .apply { setTint(textColor) }
}
