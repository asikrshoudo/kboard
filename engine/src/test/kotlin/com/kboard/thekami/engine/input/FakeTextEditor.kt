package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key

/** A tiny text field for tests: tracks text, the composing region and every call. */
internal class FakeTextEditor : TextEditor {
    val events = mutableListOf<String>()
    var contextAvailable = true
    private val buffer = StringBuilder()
    private var composingLength = 0

    val content: String get() = buffer.toString()

    /** Replaces the whole text and ends composing, like the user editing the field. */
    fun setText(value: String) {
        buffer.clear()
        buffer.append(value)
        composingLength = 0
    }

    override fun commitText(text: String) {
        replaceComposing(text)
        composingLength = 0
        events += "commit:$text"
    }

    override fun setComposingText(text: String) {
        replaceComposing(text)
        composingLength = text.length
        events += "compose:$text"
    }

    override fun finishComposing() {
        composingLength = 0
        events += "finish"
    }

    override fun replaceBeforeCursor(deleteLength: Int, text: String) {
        buffer.setLength(buffer.length - deleteLength)
        buffer.append(text)
        composingLength = 0
        events += "replace:$deleteLength:$text"
    }

    override fun deleteBackward() {
        if (buffer.isNotEmpty()) buffer.deleteCharAt(buffer.lastIndex)
        events += "delete"
    }

    override fun performEnter() {
        events += "enter"
    }

    override fun textBeforeCursor(length: Int): String? =
        if (contextAvailable) buffer.takeLast(length).toString() else null

    private fun replaceComposing(newText: String) {
        buffer.setLength(buffer.length - composingLength)
        buffer.append(newText)
    }
}

internal fun controlKey(action: ControlAction) = Key.Control(action, 1f)

internal fun letterKey(char: Char) = Key.Character(base = char.toString(), shifted = char.uppercase())
