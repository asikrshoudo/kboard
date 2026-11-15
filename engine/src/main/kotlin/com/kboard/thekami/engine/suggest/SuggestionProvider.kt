package com.kboard.thekami.engine.suggest

import com.kboard.thekami.engine.phonetic.PhoneticTransliterator

/**
 * Suggests words from what the user has typed before. While a Bangla word is
 * being composed it offers the spelling variants instead. Never blocks: it
 * only calls the [WordStore] and does small calculations.
 */
class SuggestionProvider(private val store: WordStore, private val clock: () -> Long) {
    suspend fun suggest(request: SuggestionRequest): List<Suggestion> =
        if (request.composingLatin.isEmpty()) {
            wordSuggestions(request.textBeforeCursor)
        } else {
            banglaSuggestions(request.composingLatin)
        }

    /** Completes the word being typed, or predicts the next word after a space. */
    private suspend fun wordSuggestions(textBeforeCursor: String): List<Suggestion> {
        val context = WordContext.parse(textBeforeCursor)
        val prefix = context.current.lowercase()
        val now = clock()
        val scores = HashMap<String, Double>()
        if (prefix.isNotEmpty()) {
            store.wordsStartingWith(prefix, LOOKUP_LIMIT).forEach { add(scores, it, Ranker.score(it, now)) }
        }
        context.previous?.let { previous ->
            store.followersOf(previous.lowercase(), prefix, LOOKUP_LIMIT)
                .forEach { add(scores, it, Ranker.score(it, now) * PAIR_WEIGHT) }
        }
        return scores.entries
            .filter { it.key != prefix }
            .sortedWith(compareByDescending<Map.Entry<String, Double>> { it.value }.thenBy { it.key })
            .take(MAX_SUGGESTIONS)
            .map { Suggestion(matchCase(it.key, context.current), context.current.length) }
    }

    /** The default spelling, the best alternative (a learned one first), and the Latin text. */
    private suspend fun banglaSuggestions(latin: String): List<Suggestion> {
        val shown = PhoneticTransliterator.transliterate(latin)
        val variants = BanglaVariants.of(latin)
            .map { PhoneticTransliterator.transliterate(it) }
            .filter { it != shown }
            .distinct()
        val now = clock()
        val learned = (store.wordsStartingWith(shown, LOOKUP_LIMIT) + store.statsOf(variants))
            .filter { it.word != shown }
            .distinctBy { it.word }
            .sortedByDescending { Ranker.score(it, now) }
            .map { it.word }
        val alternative = (learned + variants).firstOrNull()
        return listOfNotNull(shown, alternative, latin).distinct().map { Suggestion(it) }
    }

    private fun add(scores: MutableMap<String, Double>, stat: WordStat, score: Double) {
        scores[stat.word] = (scores[stat.word] ?: 0.0) + score
    }

    /** Words are stored in lowercase; a capital letter typed by the user is kept. */
    private fun matchCase(word: String, typed: String): String =
        if (typed.firstOrNull()?.isUpperCase() == true) word.replaceFirstChar { it.uppercase() } else word

    private companion object {
        const val MAX_SUGGESTIONS = 3
        const val LOOKUP_LIMIT = 20

        /** A word that followed the previous word before counts this much more. */
        const val PAIR_WEIGHT = 2.0
    }
}
