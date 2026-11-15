package com.kboard.thekami.ime.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import com.kboard.thekami.engine.layout.Layer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorTraitsTest {
    private val text = InputType.TYPE_CLASS_TEXT

    @Test
    fun passwordFieldsAreSensitive() {
        listOf(
            InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
        ).forEach { assertTrue(EditorTraits.isSensitive(text or it)) }
    }

    @Test
    fun numericPasswordsAreSensitive() {
        val pin = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        assertTrue(EditorTraits.isSensitive(pin))
    }

    @Test
    fun emailAndWebAddressFieldsAreSensitive() {
        listOf(
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
            InputType.TYPE_TEXT_VARIATION_URI,
        ).forEach { assertTrue(EditorTraits.isSensitive(text or it)) }
    }

    @Test
    fun ordinaryFieldsAreNotSensitive() {
        assertFalse(EditorTraits.isSensitive(text))
        assertFalse(EditorTraits.isSensitive(text or InputType.TYPE_TEXT_FLAG_MULTI_LINE))
        assertFalse(EditorTraits.isSensitive(InputType.TYPE_CLASS_NUMBER))
    }

    @Test
    fun theNoSuggestionsFlagIsDetectedInTextFields() {
        assertTrue(EditorTraits.wantsNoSuggestions(text or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS))
        assertFalse(EditorTraits.wantsNoSuggestions(text))
    }

    @Test
    fun theOptOutFlagIsDetected() {
        val withFlag = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING or EditorInfo.IME_ACTION_SEND
        assertTrue(EditorTraits.noPersonalizedLearning(withFlag))
        assertFalse(EditorTraits.noPersonalizedLearning(EditorInfo.IME_ACTION_SEND))
    }

    @Test
    fun numberAndPhoneFieldsOpenOnTheSymbolsPage() {
        assertEquals(Layer.SYMBOLS, EditorTraits.startLayer(InputType.TYPE_CLASS_NUMBER))
        assertEquals(Layer.SYMBOLS, EditorTraits.startLayer(InputType.TYPE_CLASS_PHONE))
        assertEquals(Layer.LETTERS, EditorTraits.startLayer(text))
    }

    @Test
    fun sentenceCapitalsOnlyWhenAskedForInTextFields() {
        assertTrue(EditorTraits.wantsSentenceCapitals(text or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES))
        assertFalse(EditorTraits.wantsSentenceCapitals(text))
        assertFalse(EditorTraits.wantsSentenceCapitals(InputType.TYPE_CLASS_NUMBER))
    }
}
