package com.kboard.thekami.engine.suggest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RankerTest {
    private val now = FakeWordStore.NOW

    @Test
    fun moreUsesScoreHigher() {
        assertTrue(Ranker.score(WordStat("a", 5, now), now) > Ranker.score(WordStat("a", 2, now), now))
    }

    @Test
    fun recentUseScoresHigherThanOldUse() {
        val old = now - YEAR_MS
        assertTrue(Ranker.score(WordStat("a", 3, now), now) > Ranker.score(WordStat("a", 3, old), now))
    }

    @Test
    fun anOldFavouriteStillBeatsAWordUsedOnce() {
        val old = now - YEAR_MS
        assertTrue(Ranker.score(WordStat("a", 20, old), now) > Ranker.score(WordStat("b", 1, now), now))
    }

    @Test
    fun aTimestampInTheFutureIsTreatedAsNow() {
        assertEquals(
            Ranker.score(WordStat("a", 3, now), now),
            Ranker.score(WordStat("a", 3, now + YEAR_MS), now),
            TOLERANCE,
        )
    }

    private companion object {
        const val YEAR_MS = 365L * 24 * 60 * 60 * 1000
        const val TOLERANCE = 0.0001
    }
}
