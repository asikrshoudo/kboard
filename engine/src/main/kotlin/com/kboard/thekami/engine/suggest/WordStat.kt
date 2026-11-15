package com.kboard.thekami.engine.suggest

/** How often and how recently a word (or a word pair) was used. */
data class WordStat(val word: String, val uses: Int, val lastUsedMs: Long)
