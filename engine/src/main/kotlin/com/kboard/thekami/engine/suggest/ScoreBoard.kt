package com.kboard.thekami.engine.suggest

/** Adds up the scores of candidate words and lists them best first. */
internal class ScoreBoard {
    private val scores = HashMap<String, Double>()

    val isEmpty: Boolean get() = scores.isEmpty()

    fun add(word: String, score: Double) {
        scores[word] = (scores[word] ?: 0.0) + score
    }

    /** Highest score first; words with the same score are ordered alphabetically. */
    fun ranked(): List<String> = scores.entries
        .sortedWith(compareByDescending<Map.Entry<String, Double>> { it.value }.thenBy { it.key })
        .map { it.key }
}
