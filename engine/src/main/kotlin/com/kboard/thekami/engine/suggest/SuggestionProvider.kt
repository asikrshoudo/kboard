package com.kboard.thekami.engine.suggest

import com.kboard.thekami.engine.input.SentenceCase
import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.phonetic.PhoneticTransliterator

/** Seed words score at most this, so one word the user typed outranks every seed word. */
private const val SEED_TOP_SCORE = 0.5

/** Seed words lose score the further down the list they are. */
private fun seedScore(rank: Int): Double = SEED_TOP_SCORE / (rank + 1)

/** The capital letter of the typed prefix, a sentence start and the pronoun I are kept. */
private fun matchCase(word: String, typed: String, atSentenceStart: Boolean): String {
    val isPronounI = word == "i" || word.startsWith("i'")
    val capitalize = typed.firstOrNull()?.isUpperCase() == true || atSentenceStart || isPronounI
    return if (capitalize) word.replaceFirstChar { it.uppercase() } else word
}

/**
 * Suggests words from the built-in list and from what the user has typed
 * before; the user's own words rank higher. While a Bangla word is being
 * typed it offers completions and spelling alternatives instead. Never
 * blocks: it only calls the [WordStore] and does small calculations.
 */
class SuggestionProvider(
    private val store: WordStore,
    private val seedFor: (Language) -> LanguageSeed,
    private val clock: () -> Long,
) {
    suspend fun suggest(request: SuggestionRequest): List<Suggestion> {
        val seed = seedFor(request.language)
        return if (request.composingLatin.isEmpty()) {
            wordSuggestions(request.textBeforeCursor, seed)
        } else {
            banglaSuggestions(request.composingLatin, seed)
        }
    }

    /** Completes the word being typed, or predicts the next word after a space. */
    private suspend fun wordSuggestions(textBeforeCursor: String, seed: LanguageSeed): List<Suggestion> {
        val context = WordContext.parse(textBeforeCursor)
        val prefix = context.current.lowercase()
        val board = completions(prefix, seed)
        context.previous?.let { addFollowers(board, it.lowercase(), prefix, seed) }
        if (prefix.isEmpty() && board.isEmpty) {
            seed.starters.forEachIndexed { rank, word -> board.add(word, seedScore(rank)) }
        }
        val atSentenceStart = prefix.isEmpty() && SentenceCase.startsSentence(textBeforeCursor)
        return board.ranked()
            .filter { it != prefix }
            .take(MAX_SUGGESTIONS)
            .map { Suggestion(matchCase(it, context.current, atSentenceStart), context.current.length) }
    }

    /** Words that start with [prefix]: the user's own and the built-in ones. */
    private suspend fun completions(prefix: String, seed: LanguageSeed): ScoreBoard {
        val board = ScoreBoard()
        if (prefix.isEmpty()) return board
        val now = clock()
        store.wordsStartingWith(prefix, LOOKUP_LIMIT).forEach { board.add(it.word, Ranker.score(it, now)) }
        seed.wordsStartingWith(prefix, LOOKUP_LIMIT).forEachIndexed { rank, word -> board.add(word, seedScore(rank)) }
        return board
    }

    /** Words that followed [previous] before count more than the same word on its own. */
    private suspend fun addFollowers(board: ScoreBoard, previous: String, prefix: String, seed: LanguageSeed) {
        val now = clock()
        store.followersOf(previous, prefix, LOOKUP_LIMIT)
            .forEach { board.add(it.word, Ranker.score(it, now) * PAIR_WEIGHT) }
        seed.followersOf(previous)
            .filter { it.startsWith(prefix) }
            .forEachIndexed { rank, word -> board.add(word, seedScore(rank) * PAIR_WEIGHT) }
    }

    /**
     * Two Bangla choices (completions first, then the spelling as typed, then an
     * alternative spelling, one the user chose before if there is one) and the
     * Latin text, for English words typed in Bangla mode.
     */
    private suspend fun banglaSuggestions(latin: String, seed: LanguageSeed): List<Suggestion> {
        val shown = PhoneticTransliterator.transliterate(latin)
        val variants = BanglaVariants.of(latin)
            .map { PhoneticTransliterator.transliterate(it) }
            .filter { it != shown }
            .distinct()
        val now = clock()
        val learnedVariant = store.statsOf(variants).maxByOrNull { Ranker.score(it, now) }?.word
        val completed = completions(shown, seed).ranked().filter { it != shown }
        val alternative = learnedVariant ?: variants.firstOrNull()
        val bangla = (completed.take(2) + shown + listOfNotNull(alternative)).distinct().take(2)
        return (bangla + latin).distinct().map { Suggestion(it) }
    }

    private companion object {
        const val MAX_SUGGESTIONS = 3
        const val LOOKUP_LIMIT = 20

        /** A word that followed the previous word before counts this much more. */
        const val PAIR_WEIGHT = 2.0
    }
}
