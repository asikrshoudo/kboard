package com.kboard.thekami.engine.suggest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScoreBoardTest {
    private val board = ScoreBoard()

    @Test
    fun startsEmpty() {
        assertTrue(board.isEmpty)
        assertEquals(emptyList<String>(), board.ranked())
    }

    @Test
    fun scoresOfTheSameWordAreAddedUp() {
        board.add("a", 1.0)
        board.add("b", 1.5)
        board.add("a", 1.0)
        assertFalse(board.isEmpty)
        assertEquals(listOf("a", "b"), board.ranked())
    }

    @Test
    fun equalScoresAreOrderedAlphabetically() {
        board.add("b", 1.0)
        board.add("a", 1.0)
        assertEquals(listOf("a", "b"), board.ranked())
    }
}
