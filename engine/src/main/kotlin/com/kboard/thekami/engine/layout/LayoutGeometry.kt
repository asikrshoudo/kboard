package com.kboard.thekami.engine.layout

/** Rectangle in view pixels. */
data class KeyBounds(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    fun contains(x: Float, y: Float): Boolean = x >= left && x < right && y >= top && y < bottom
}

/** A key together with the area it occupies. */
data class PlacedKey(val key: Key, val bounds: KeyBounds)

/** Pure geometry: turns a layout and a view size into key rectangles. */
object LayoutGeometry {
    /** Rows share the height equally; keys share the width by weight. Spacers are left out. */
    fun place(layout: KeyboardLayout, width: Float, height: Float): List<PlacedKey> {
        val rowHeight = height / layout.rows.size
        return layout.rows.flatMapIndexed { rowIndex, row ->
            val rowWeight = row.map { it.weight }.sum()
            val top = rowIndex * rowHeight
            var left = 0f
            row.mapNotNull { key ->
                val right = left + width * key.weight / rowWeight
                val bounds = KeyBounds(left, top, right, top + rowHeight)
                left = right
                if (key is Key.Spacer) null else PlacedKey(key, bounds)
            }
        }
    }

    /** The key under the point, or null when the point is on a spacer. */
    fun keyAt(placed: List<PlacedKey>, x: Float, y: Float): PlacedKey? =
        placed.firstOrNull { it.bounds.contains(x, y) }
}
