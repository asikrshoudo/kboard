package com.kboard.thekami.engine.layout

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LayoutGeometryTest {
    private val placed = LayoutGeometry.place(EnglishUsLayout.letters, WIDTH, HEIGHT)

    @Test
    fun placesEveryRealKeyAndNoSpacers() {
        assertEquals(KEY_COUNT, placed.size)
    }

    @Test
    fun keysStayInsideTheView() {
        placed.forEach {
            with(it.bounds) {
                assertTrue(left >= 0f && top >= 0f)
                assertTrue(right <= WIDTH + TOLERANCE && bottom <= HEIGHT + TOLERANCE)
            }
        }
    }

    @Test
    fun rowsShareTheHeightEqually() {
        val bounds = placed.first().bounds
        assertEquals(ROW_HEIGHT, bounds.bottom - bounds.top, TOLERANCE)
    }

    @Test
    fun widthFollowsWeight() {
        val space = placed.first { (it.key as? Key.Control)?.action == ControlAction.SPACE }.bounds
        val comma = placed.first { (it.key as? Key.Character)?.base == "," }.bounds
        assertEquals(SPACE_TO_COMMA, (space.right - space.left) / (comma.right - comma.left), TOLERANCE)
    }

    @Test
    fun keyAtFindsTheKeyUnderThePoint() {
        val first = placed.first()
        val x = (first.bounds.left + first.bounds.right) / 2
        val y = (first.bounds.top + first.bounds.bottom) / 2
        assertEquals(first, LayoutGeometry.keyAt(placed, x, y))
    }

    @Test
    fun keyAtIgnoresSpacerArea() {
        assertNull(LayoutGeometry.keyAt(placed, 1f, ROW_HEIGHT * 1.5f))
    }

    private companion object {
        const val WIDTH = 1000f
        const val HEIGHT = 400f
        const val ROW_HEIGHT = 100f
        const val TOLERANCE = 0.001f
        const val KEY_COUNT = 33 // 10 + 9 + 9 + 5 keys per row, spacers excluded
        const val SPACE_TO_COMMA = 4.5f
    }
}
