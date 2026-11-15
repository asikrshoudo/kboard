package com.kboard.thekami.data.learning

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The on-device learning database. Schema export is off while the schema is
 * still changing; turn it on and add migrations before the first public release.
 */
@Database(entities = [WordEntity::class, BigramEntity::class], version = 1, exportSchema = false)
abstract class LearningDatabase : RoomDatabase() {
    abstract fun dao(): LearningDao

    companion object {
        private const val NAME = "kboard_learning.db"

        @Volatile
        private var instance: LearningDatabase? = null

        /** One database per process, shared by the keyboard service and the app. */
        fun get(context: Context): LearningDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, LearningDatabase::class.java, NAME)
                .build()
                .also { instance = it }
        }
    }
}
