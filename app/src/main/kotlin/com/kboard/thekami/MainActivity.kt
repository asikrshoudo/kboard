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
import com.kboard.thekami.ui.HomeScreen
import com.kboard.thekami.ui.SettingsViewModel
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
        setContent {
            KboardTheme {
                val settings by viewModel.settings.collectAsState()
                HomeScreen(status, resumeCount, settings, viewModel::setHapticsEnabled)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeCount++
    }
}
