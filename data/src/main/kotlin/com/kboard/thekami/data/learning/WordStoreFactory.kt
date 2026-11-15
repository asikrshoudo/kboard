package com.kboard.thekami.data.learning

import android.content.Context
import com.kboard.thekami.engine.suggest.WordStore

/** The learned-words store. All data stays in this app's private storage. */
fun createWordStore(context: Context): WordStore = RoomWordStore(LearningDatabase.get(context).dao())
