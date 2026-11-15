package com.kboard.thekami.engine.suggest

/** Where learned words live. The engine only knows this interface, never the database. */
interface WordStore {
    /** Counts one use of [word], and of the pair ([previous], [word]) when [previous] is not null. */
    suspend fun record(previous: String?, word: String, nowMs: Long)

    /** Most used words that start with [prefix]. */
    suspend fun wordsStartingWith(prefix: String, limit: Int): List<WordStat>

    /** Stats of exactly these words; words that were never used are left out. */
    suspend fun statsOf(words: List<String>): List<WordStat>

    /** Most used words that followed [previous] and start with [prefix] (which may be empty). */
    suspend fun followersOf(previous: String, prefix: String, limit: Int): List<WordStat>

    /** Forgets everything that was learned. */
    suspend fun clear()
}
