package com.kboard.thekami.data.learning

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A word and how often and when it was used. Stored in lowercase. */
@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val word: String,
    val uses: Int,
    val lastUsedMs: Long,
)

/** How often [word] followed [previous]. */
@Entity(tableName = "bigrams", primaryKeys = ["previous", "word"])
data class BigramEntity(
    val previous: String,
    val word: String,
    val uses: Int,
    val lastUsedMs: Long,
)
