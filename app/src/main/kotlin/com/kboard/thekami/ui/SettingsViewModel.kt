package com.kboard.thekami.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kboard.thekami.data.settings.KeyboardSettings
import com.kboard.thekami.data.settings.SettingsRepository
import com.kboard.thekami.data.settings.SpaceLongPress
import com.kboard.thekami.engine.suggest.WordStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val wordStore: WordStore,
) : ViewModel() {
    val settings: StateFlow<KeyboardSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = KeyboardSettings(),
    )

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setHapticsEnabled(enabled) }
    }

    fun setAutocorrectEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setAutocorrectEnabled(enabled) }
    }

    fun setIncognitoEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setIncognitoEnabled(enabled) }
    }

    fun setNumberRowEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.appearance.setNumberRowEnabled(enabled) }
    }

    fun setKeyPreviewEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.appearance.setKeyPreviewEnabled(enabled) }
    }

    fun setKeyboardHeightPercent(percent: Int) {
        viewModelScope.launch { repository.appearance.setHeightPercent(percent) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.sound.setEnabled(enabled) }
    }

    fun setSoundVolumePercent(percent: Int) {
        viewModelScope.launch { repository.sound.setVolumePercent(percent) }
    }

    fun setSpaceLongPress(action: SpaceLongPress) {
        viewModelScope.launch { repository.behavior.setSpaceLongPress(action) }
    }

    /** Deletes every learned word and word pair, and the corrections the user had undone. */
    fun resetLearnedData() {
        viewModelScope.launch {
            wordStore.clear()
            repository.clearIgnoredCorrections()
        }
    }

    private companion object {
        /** Keeps the settings flow alive across short pauses such as screen rotation. */
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
