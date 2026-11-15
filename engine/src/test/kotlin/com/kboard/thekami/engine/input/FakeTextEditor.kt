package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.layout.ControlAction
import com.kboard.thekami.engine.layout.Key

/** A tiny text field for tests: tracks text, the composing region and every call. */
internal class FakeTextEditor : TextEditor {
    val events = mutableListOf<String>()
    var contextAvailable = true
    private val text = StringBuilder()
    private var composingLength = 0

    val content: String get() = text.toString()

    /** Replaces the whole text and ends composing, like the user editing the field. */
    fun setText(value: String) {
        text.clear()
        text.append(value)
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

    override fun deleteBackward() {
        if (text.isNotEmpty()) text.deleteCharAt(text.lastIndex)
        events += "delete"
    }

    override fun performEnter() {
        events += "enter"
    }

    override fun textBeforeCursor(length: Int): String? =
        if (contextAvailable) text.takeLast(length).toString() else null

    private fun replaceComposing(newText: String) {
        text.setLength(text.length - composingLength)
        text.append(newText)
    }
}

internal fun controlKey(action: ControlAction) = Key.Control(action, 1f)

internal fun letterKey(char: Char) = Key.Character(base = char.toString(), shifted = char.uppercase())
