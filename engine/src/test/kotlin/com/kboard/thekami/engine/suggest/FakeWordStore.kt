package com.kboard.thekami.engine.suggest

/** An in-memory [WordStore] for tests. */
internal class FakeWordStore : WordStore {
    private val words = mutableMapOf<String, WordStat>()
    private val pairs = mutableMapOf<Pair<String, String>, WordStat>()

    fun addWord(word: String, uses: Int, lastUsedMs: Long = NOW) {
        words[word] = WordStat(word, uses, lastUsedMs)
    }

    fun addPair(previous: String, word: String, uses: Int, lastUsedMs: Long = NOW) {
        pairs[previous to word] = WordStat(word, uses, lastUsedMs)
    }

    override suspend fun record(previous: String?, word: String, nowMs: Long) {
        val old = words[word]
        words[word] = WordStat(word, (old?.uses ?: 0) + 1, nowMs)
        if (previous != null) {
            val oldPair = pairs[previous to word]
            pairs[previous to word] = WordStat(word, (oldPair?.uses ?: 0) + 1, nowMs)
        }
    }

    override suspend fun wordsStartingWith(prefix: String, limit: Int): List<WordStat> =
        words.values.filter { it.word.startsWith(prefix) }.sortedByDescending { it.uses }.take(limit)

    override suspend fun statsOf(words: List<String>): List<WordStat> = words.mapNotNull { this.words[it] }

    override suspend fun followersOf(previous: String, prefix: String, limit: Int): List<WordStat> =
        pairs.filterKeys { it.first == previous }.values
            .filter { it.word.startsWith(prefix) }
            .sortedByDescending { it.uses }
            .take(limit)

    override suspend fun clear() {
        words.clear()
        pairs.clear()
    }

    companion object {
        const val NOW = 1_000_000_000_000L
    }
}
