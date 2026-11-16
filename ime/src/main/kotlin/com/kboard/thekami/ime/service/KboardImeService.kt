package com.kboard.thekami.ime.service

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import com.kboard.thekami.data.learning.createWordStore
import com.kboard.thekami.data.settings.KeyboardAppearance
import com.kboard.thekami.data.settings.KeyboardSettings
import com.kboard.thekami.data.settings.createSettingsRepository
import com.kboard.thekami.engine.input.KeyInputHandler
import com.kboard.thekami.engine.language.Language
import com.kboard.thekami.engine.layout.Key
import com.kboard.thekami.engine.layout.Layouts
import com.kboard.thekami.engine.suggest.LearningPolicy
import com.kboard.thekami.engine.suggest.Seeds
import com.kboard.thekami.engine.suggest.Suggestion
import com.kboard.thekami.engine.suggest.SuggestionProvider
import com.kboard.thekami.engine.suggest.SuggestionRequest
import com.kboard.thekami.ime.feedback.KeyHaptics
import com.kboard.thekami.ime.input.EditorTraits
import com.kboard.thekami.ime.input.InputConnectionEditor
import com.kboard.thekami.ime.render.KeyListener
import com.kboard.thekami.ime.render.KeyboardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * The system-facing keyboard service. Wires the view to input, feedback,
 * settings, suggestions, autocorrect and learning, and applies the privacy
 * rules for each text field.
 */
class KboardImeService : InputMethodService() {
    // Main dispatcher: the input handler is only ever touched on the main thread.
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val editor = InputConnectionEditor(
        connection = { currentInputConnection },
        editorInfo = { currentInputEditorInfo },
    )
    private val wordStore by lazy { createWordStore(applicationContext) }
    private val settingsRepository by lazy { createSettingsRepository(applicationContext) }
    private val provider by lazy { SuggestionProvider(wordStore, Seeds::of, System::currentTimeMillis) }
    private val haptics by lazy { KeyHaptics(this) }
    private val inputHandler = KeyInputHandler(
        editor,
        corrections = { word -> serviceScope.launch { settingsRepository.addIgnoredCorrection(word) } },
    ) { previous, word ->
        serviceScope.launch { wordStore.record(previous, word, System.currentTimeMillis()) }
    }

    private var keyboardView: KeyboardView? = null
    private var suggestions: List<Suggestion> = emptyList()
    private var suggestionJob: Job? = null

    private var hapticsEnabled = KeyboardSettings().hapticsEnabled
    private var incognito = KeyboardSettings().incognitoEnabled
    private var autocorrectEnabled = KeyboardSettings().autocorrectEnabled
    private var appearance = KeyboardAppearance()

    // Facts about the focused text field, set in onStartInputView.
    private var sensitiveField = false
    private var noLearning = false
    private var noSuggestions = false

    private val keyListener = object : KeyListener {
        override fun onKeyPress(key: Key) {
            if (hapticsEnabled) haptics.perform(key)
            typeKey(key)
        }

        override fun onKeyRepeat(key: Key) = typeKey(key)

        override fun onSuggestionPick(index: Int) {
            val suggestion = suggestions.getOrNull(index) ?: return
            if (hapticsEnabled) haptics.suggestionSelected()
            inputHandler.acceptSuggestion(suggestion)
            showCurrentState()
        }
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            inputHandler.setLanguage(Language.fromId(settingsRepository.settings.first().languageId))
            showCurrentState()
            settingsRepository.settings.collect {
                hapticsEnabled = it.hapticsEnabled
                incognito = it.incognitoEnabled
                autocorrectEnabled = it.autocorrectEnabled
                appearance = it.appearance
                inputHandler.ignoredCorrections = it.ignoredCorrections
                applyFieldRules()
                // A changed number row, height or preview choice shows up at once.
                showCurrentState()
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        val layout = Layouts.layoutFor(inputHandler.language, inputHandler.layer, appearance.numberRowEnabled)
        return KeyboardView(this, layout, keyListener).also {
            keyboardView = it
            showCurrentState()
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        val inputType = info?.inputType ?: 0
        sensitiveField = EditorTraits.isSensitive(inputType)
        noLearning = EditorTraits.noPersonalizedLearning(info?.imeOptions ?: 0)
        noSuggestions = EditorTraits.wantsNoSuggestions(inputType)
        inputHandler.start(
            startLayer = EditorTraits.startLayer(inputType),
            autoCapitalize = EditorTraits.wantsSentenceCapitals(inputType),
        )
        applyFieldRules()
        showCurrentState()
    }

    /**
     * The transparent area above the suggestion strip floats over the app
     * instead of pushing it up, and touches there reach the app underneath.
     */
    override fun onComputeInsets(outInsets: Insets) {
        super.onComputeInsets(outInsets)
        val overlay = keyboardView?.overlayHeight ?: return
        outInsets.contentTopInsets += overlay
        outInsets.visibleTopInsets += overlay
    }

    private fun typeKey(key: Key) {
        val languageBefore = inputHandler.language
        inputHandler.onKey(key)
        if (inputHandler.language != languageBefore) {
            val id = inputHandler.language.id
            serviceScope.launch { settingsRepository.setLanguageId(id) }
        }
        showCurrentState()
    }

    /** Learning, suggestions and autocorrect each depend on the field and on the user's settings. */
    private fun applyFieldRules() {
        inputHandler.learningAllowed = LearningPolicy.isLearningAllowed(sensitiveField, noLearning, incognito)
        inputHandler.autocorrectAllowed = autocorrectEnabled && suggestionsAllowed()
    }

    private fun suggestionsAllowed(): Boolean = LearningPolicy.isSuggestingAllowed(sensitiveField) && !noSuggestions

    private fun showCurrentState() {
        val view = keyboardView ?: return
        val numberRow = appearance.numberRowEnabled
        view.setLayout(
            Layouts.layoutFor(inputHandler.language, inputHandler.layer, numberRow),
            Layouts.rowCount(numberRow),
        )
        view.heightPercent = appearance.heightPercent
        view.keyPreviewEnabled = appearance.keyPreviewEnabled
        view.shiftActive = inputHandler.shifted
        view.spaceLabel = inputHandler.language.nativeName
        refreshSuggestions(view)
    }

    private fun refreshSuggestions(view: KeyboardView) {
        suggestionJob?.cancel()
        if (!suggestionsAllowed()) {
            suggestions = emptyList()
            view.setSuggestions(emptyList())
            return
        }
        val request = SuggestionRequest(
            language = inputHandler.language,
            composingLatin = inputHandler.composingLatin,
            textBeforeCursor = editor.textBeforeCursor(CONTEXT_CHARS).orEmpty(),
        )
        suggestionJob = serviceScope.launch {
            suggestions = provider.suggest(request)
            view.setSuggestions(suggestions.map { it.text })
        }
    }

    private companion object {
        /** Enough text before the cursor to find the current and the previous word. */
        const val CONTEXT_CHARS = 64
    }
}
