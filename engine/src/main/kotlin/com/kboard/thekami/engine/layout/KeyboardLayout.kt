package com.kboard.thekami.engine.layout

/** A keyboard described as rows of keys, independent of how it is drawn. */
data class KeyboardLayout(val rows: List<List<Key>>)
