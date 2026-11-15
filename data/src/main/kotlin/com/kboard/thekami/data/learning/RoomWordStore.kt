package com.kboard.thekami.data.learning

import com.kboard.thekami.engine.suggest.WordStat
import com.kboard.thekami.engine.suggest.WordStore

/** [WordStore] backed by the Room database. */
internal class RoomWordStore(private val dao: LearningDao) : WordStore {
    override suspend fun record(previous: String?, word: String, nowMs: Long) =
        dao.record(previous, word, nowMs)

    override suspend fun wordsStartingWith(prefix: String, limit: Int): List<WordStat> =
        dao.wordsStartingWith(prefix, limit).map { WordStat(it.word, it.uses, it.lastUsedMs) }

    override suspend fun statsOf(words: List<String>): List<WordStat> =
        if (words.isEmpty()) emptyList() else dao.statsOf(words).map { WordStat(it.word, it.uses, it.lastUsedMs) }

    override suspend fun followersOf(previous: String, prefix: String, limit: Int): List<WordStat> =
        dao.followersOf(previous, prefix, limit).map { WordStat(it.word, it.uses, it.lastUsedMs) }

    override suspend fun clear() = dao.clearAll()
}
