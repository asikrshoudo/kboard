package com.kboard.thekami.engine.suggest

/**
 * Hand-picked common words that give a new user useful suggestions before the
 * keyboard has learned anything. [words] are ordered most common first.
 * [followers] maps a word to the words that commonly come next, and [starters]
 * are common words to offer where there is nothing else to go on.
 */
class LanguageSeed(
    private val words: List<String>,
    private val followers: Map<String, List<String>>,
    val starters: List<String>,
) {
    fun wordsStartingWith(prefix: String, limit: Int): List<String> =
        words.filter { it.startsWith(prefix) }.take(limit)

    fun followersOf(previous: String): List<String> = followers[previous].orEmpty()

    companion object {
        val EMPTY = LanguageSeed(emptyList(), emptyMap(), emptyList())

        private val WHITESPACE = Regex("\\s+")

        /**
         * Builds a seed from plain text: words separated by spaces, one
         * `word: next next` line per follower rule.
         */
        fun parse(wordsText: String, followersText: String, startersText: String): LanguageSeed = LanguageSeed(
            words = tokens(wordsText),
            followers = followersText.lines().map { it.trim() }.filter { it.isNotEmpty() }.associate { line ->
                val colon = line.indexOf(':')
                require(colon > 0) { "Bad follower line: $line" }
                line.substring(0, colon).trim() to tokens(line.substring(colon + 1))
            },
            starters = tokens(startersText),
        )

        private fun tokens(text: String): List<String> = text.trim().split(WHITESPACE).filter { it.isNotEmpty() }
    }
}
