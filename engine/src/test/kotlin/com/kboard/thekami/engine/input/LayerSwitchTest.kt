package com.kboard.thekami.engine.input

import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.Layer
import org.junit.Assert.assertEquals
import org.junit.Test

class LayerSwitchTest {
    private val editor = FakeTextEditor()
    private val handler = KeyInputHandler(editor)

    @Test
    fun showLayerChangesThePage() {
        handler.showLayer(Layer.NUMPAD)
        assertEquals(Layer.NUMPAD, handler.layer)
    }

    @Test
    fun digitsOnTheNumpadAreTypedAsTheyAre() {
        handler.showLayer(Layer.NUMPAD)
        handler.onKey(Key.Character(base = "5", shifted = "5"))
        assertEquals("5", editor.content)
    }

    @Test
    fun switchingPageKeepsABanglaWordThatIsBeingTyped() {
        handler.setLanguage(Language.BANGLA_PHONETIC)
        "am".forEach { handler.onKey(letterKey(it)) }
        handler.showLayer(Layer.NUMPAD)
        assertEquals("আম", editor.content)
        assertEquals("", handler.composingLatin)
    }
}
