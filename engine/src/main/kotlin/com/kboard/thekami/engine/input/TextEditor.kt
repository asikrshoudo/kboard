package com.kboard.thekami.engine.input

/** What the keyboard needs from the text field it types into. */
interface TextEditor {
    fun commitText(text: String)

    /** Deletes the selection, or the character before the cursor. */
    fun deleteBackward()

    /** Runs the field's action (Send, Search, ...) or inserts a new line. */
    fun performEnter()
}
