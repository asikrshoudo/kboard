package com.kboard.thekami.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import com.kboard.thekami.engine.layout.Layer

/**
 * Reads what a text field asks of the keyboard. Works on the raw flag values
 * so the privacy rules can be unit-tested without a device.
 */
internal object EditorTraits {
    private val SENSITIVE_TEXT_VARIATIONS = setOf(
        InputType.TYPE_TEXT_VARIATION_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
        InputType.TYPE_TEXT_VARIATION_URI,
    )

    /**
     * Number fields open on the number pad. Phone fields open on the symbols
     * page, where the digits and the signs a phone number needs are.
     */
    fun startLayer(inputType: Int): Layer = when (inputClass(inputType)) {
        InputType.TYPE_CLASS_NUMBER -> Layer.NUMPAD
        InputType.TYPE_CLASS_PHONE -> Layer.SYMBOLS
        else -> Layer.LETTERS
    }

    /** True when the field asks for a capital letter at the start of each sentence. */
    fun wantsSentenceCapitals(inputType: Int): Boolean =
        inputClass(inputType) == InputType.TYPE_CLASS_TEXT &&
            (inputType and InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) != 0

    /** Passwords, email addresses and web addresses: no learning and no suggestions. */
    fun isSensitive(inputType: Int): Boolean {
        val variation = inputType and InputType.TYPE_MASK_VARIATION
        return when (inputClass(inputType)) {
            InputType.TYPE_CLASS_TEXT -> variation in SENSITIVE_TEXT_VARIATIONS
            InputType.TYPE_CLASS_NUMBER -> variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD
            else -> false
        }
    }

    /** True when the app asked for no suggestions or corrections in this field. */
    fun wantsNoSuggestions(inputType: Int): Boolean =
        inputClass(inputType) == InputType.TYPE_CLASS_TEXT &&
            (inputType and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0

    /** True when the app asked keyboards not to learn from this field. */
    fun noPersonalizedLearning(imeOptions: Int): Boolean =
        (imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING) != 0

    private fun inputClass(inputType: Int): Int = inputType and InputType.TYPE_MASK_CLASS
}
