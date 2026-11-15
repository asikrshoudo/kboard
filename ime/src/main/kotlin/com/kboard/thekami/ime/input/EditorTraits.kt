package com.kboard.thekami.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import com.kboard.thekami.engine.layout.Layer

/** Reads what the focused text field asks of the keyboard. */
internal object EditorTraits {
    /** Number and phone fields open on the symbols page, where the digits are. */
    fun startLayer(info: EditorInfo?): Layer = when (inputClass(info)) {
        InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_PHONE -> Layer.SYMBOLS
        else -> Layer.LETTERS
    }

    /** True when the field asks for a capital letter at the start of each sentence. */
    fun wantsSentenceCapitals(info: EditorInfo?): Boolean {
        val inputType = info?.inputType ?: return false
        return inputClass(info) == InputType.TYPE_CLASS_TEXT &&
            (inputType and InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) != 0
    }

    private fun inputClass(info: EditorInfo?): Int = (info?.inputType ?: 0) and InputType.TYPE_MASK_CLASS
}
