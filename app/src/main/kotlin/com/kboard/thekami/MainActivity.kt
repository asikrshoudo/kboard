package com.kboard.thekami

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.kboard.thekami.setup.KeyboardStatus
import com.kboard.thekami.ui.GestureActions
import com.kboard.thekami.ui.HomeActions
import com.kboard.thekami.ui.HomeScreen
import com.kboard.thekami.ui.LayoutActions
import com.kboard.thekami.ui.SettingsViewModel
import com.kboard.thekami.ui.SoundActions
import com.kboard.thekami.ui.theme.KboardTheme
import dagger.hilt.android.AndroidEntryPoint

/** Companion app: setup guide and settings for the keyboard. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: SettingsViewModel by viewModels()
    private var resumeCount by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val status = KeyboardStatus(this)
        val actions = HomeActions(
            onHapticsChange = viewModel::setHapticsEnabled,
            onAutocorrectChange = viewModel::setAutocorrectEnabled,
            onIncognitoChange = viewModel::setIncognitoEnabled,
            onResetLearning = viewModel::resetLearnedData,
            layout = LayoutActions(
                onNumberRowChange = viewModel::setNumberRowEnabled,
                onKeyPreviewChange = viewModel::setKeyPreviewEnabled,
                onHeightChange = viewModel::setKeyboardHeightPercent,
            ),
            sound = SoundActions(
                onEnabledChange = viewModel::setSoundEnabled,
                onVolumeChange = viewModel::setSoundVolumePercent,
            ),
            gestures = GestureActions(onSpaceLongPressChange = viewModel::setSpaceLongPress),
        )
        setContent {
            KboardTheme {
                val settings by viewModel.settings.collectAsState()
                HomeScreen(status, resumeCount, settings, actions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeCount++
    }
}
