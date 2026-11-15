package com.kboard.thekami.data.learning

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/** Database access for learned words. Used through [RoomWordStore]. */
@Dao
abstract class LearningDao {
    @Query("UPDATE words SET uses = uses + 1, lastUsedMs = :now WHERE word = :word")
    abstract suspend fun bumpWord(word: String, now: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertWord(entity: WordEntity)

    @Query("UPDATE bigrams SET uses = uses + 1, lastUsedMs = :now WHERE previous = :previous AND word = :word")
    abstract suspend fun bumpBigram(previous: String, word: String, now: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertBigram(entity: BigramEntity)

    /** Counts one use of the word, and of the pair when there is a previous word. */
    @Transaction
    open suspend fun record(previous: String?, word: String, nowMs: Long) {
        if (bumpWord(word, nowMs) == 0) insertWord(WordEntity(word, 1, nowMs))
        if (previous != null && bumpBigram(previous, word, nowMs) == 0) {
            insertBigram(BigramEntity(previous, word, 1, nowMs))
        }
    }

    @Query(
        "SELECT * FROM words WHERE substr(word, 1, length(:prefix)) = :prefix " +
            "ORDER BY uses DESC LIMIT :limit",
    )
    abstract suspend fun wordsStartingWith(prefix: String, limit: Int): List<WordEntity>

    @Query("SELECT * FROM words WHERE word IN (:words)")
    abstract suspend fun statsOf(words: List<String>): List<WordEntity>

    @Query(
        "SELECT * FROM bigrams WHERE previous = :previous AND substr(word, 1, length(:prefix)) = :prefix " +
            "ORDER BY uses DESC LIMIT :limit",
    )
    abstract suspend fun followersOf(previous: String, prefix: String, limit: Int): List<BigramEntity>

    @Query("DELETE FROM words")
    abstract suspend fun deleteWords()

    @Query("DELETE FROM bigrams")
    abstract suspend fun deleteBigrams()

    @Transaction
    open suspend fun clearAll() {
        deleteWords()
        deleteBigrams()
    }
}
