package com.kboard.thekami.ime.input

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.kboard.thekami.engine.input.TextEditor

/**
 * [TextEditor] backed by the current Android [InputConnection]. Both are read
 * through lambdas because the system replaces them whenever the focused field
 * changes.
 */
internal class InputConnectionEditor(
    private val connection: () -> InputConnection?,
    private val editorInfo: () -> EditorInfo?,
) : TextEditor {

    override fun commitText(text: String) {
        connection()?.commitText(text, CURSOR_AFTER_TEXT)
    }

    override fun setComposingText(text: String) {
        connection()?.setComposingText(text, CURSOR_AFTER_TEXT)
    }

    override fun finishComposing() {
        connection()?.finishComposingText()
    }

    override fun replaceBeforeCursor(deleteLength: Int, text: String) {
        val input = connection() ?: return
        input.beginBatchEdit()
        input.deleteSurroundingText(deleteLength, 0)
        input.commitText(text, CURSOR_AFTER_TEXT)
        input.endBatchEdit()
    }

    override fun deleteBackward() {
        val input = connection() ?: return
        if (input.getSelectedText(NO_FLAGS).isNullOrEmpty()) {
            // Code points, so emoji and other surrogate pairs are removed whole.
            input.deleteSurroundingTextInCodePoints(1, 0)
        } else {
            input.commitText("", CURSOR_AFTER_TEXT)
        }
    }

    override fun textBeforeCursor(length: Int): String? =
        connection()?.getTextBeforeCursor(length, NO_FLAGS)?.toString()

    override fun performEnter() {
        val input = connection() ?: return
        val options = editorInfo()?.imeOptions ?: 0
        val action = options and EditorInfo.IME_MASK_ACTION
        val hasAction = action != EditorInfo.IME_ACTION_NONE &&
            action != EditorInfo.IME_ACTION_UNSPECIFIED &&
            (options and EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0
        if (hasAction) {
            input.performEditorAction(action)
        } else {
            input.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            input.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    private companion object {
        /** commitText argument: put the cursor right after the inserted text. */
        const val CURSOR_AFTER_TEXT = 1

        /** getTextBeforeCursor and getSelectedText argument: no special flags. */
        const val NO_FLAGS = 0
    }
}
