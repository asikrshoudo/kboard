package com.kboard.thekami.engine.suggest

import kotlin.math.pow

/** Scores learned words: frequent words rank high, recently used ones a little higher. */
object Ranker {
    private const val HALF_LIFE_DAYS = 30
    private const val MS_PER_DAY = 86_400_000.0
    private const val HALVING = 0.5

    /** Old words keep this share of their score, so a favourite word never disappears. */
    private const val RECENCY_FLOOR = 0.25

    fun score(stat: WordStat, nowMs: Long): Double {
        val ageDays = (nowMs - stat.lastUsedMs).coerceAtLeast(0) / MS_PER_DAY
        val recency = HALVING.pow(ageDays / HALF_LIFE_DAYS)
        return stat.uses * (RECENCY_FLOOR + (1 - RECENCY_FLOOR) * recency)
    }
}
