package com.kboard.thekami.engine.input

/** What the keyboard needs from the text field it types into. */
interface TextEditor {
    fun commitText(text: String)

    /** Shows [text] as the word being composed, replacing the previous composing text. */
    fun setComposingText(text: String)

    /** Turns the composing text into ordinary text. */
    fun finishComposing()

    /** Deletes the selection, or the character before the cursor. */
    fun deleteBackward()

    /** Runs the field's action (Send, Search, ...) or inserts a new line. */
    fun performEnter()

    /** Up to [length] characters before the cursor, or null if the field does not share them. */
    fun textBeforeCursor(length: Int): String?
}
