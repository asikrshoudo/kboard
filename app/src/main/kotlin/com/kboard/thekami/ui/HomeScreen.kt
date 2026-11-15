package com.kboard.thekami.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kboard.thekami.R
import com.kboard.thekami.data.settings.KeyboardSettings
import com.kboard.thekami.setup.KeyboardStatus

/** What the user can change on the home screen. */
class HomeActions(
    val onHapticsChange: (Boolean) -> Unit,
    val onAutocorrectChange: (Boolean) -> Unit,
    val onIncognitoChange: (Boolean) -> Unit,
    val onResetLearning: () -> Unit,
)

/**
 * The only screen. [refreshKey] changes every time the app returns to the
 * foreground, so the setup steps re-check the system state.
 */
@Composable
fun HomeScreen(
    status: KeyboardStatus,
    refreshKey: Int,
    settings: KeyboardSettings,
    actions: HomeActions,
) {
    val enabled = remember(refreshKey) { status.isEnabled() }
    val selected = remember(refreshKey) { status.isSelected() }
    val spacing = dimensionResource(R.dimen.spacing_medium)
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge)
            Text(text = stringResource(R.string.tagline), style = MaterialTheme.typography.bodyLarge)
            SetupCard(enabled, selected, status::openInputSettings, status::showPicker)
            TypingCard(
                hapticsEnabled = settings.hapticsEnabled,
                autocorrectEnabled = settings.autocorrectEnabled,
                onHapticsChange = actions.onHapticsChange,
                onAutocorrectChange = actions.onAutocorrectChange,
            )
            LearningCard(settings.incognitoEnabled, actions.onIncognitoChange, actions.onResetLearning)
            TryItCard()
            PrivacyCard()
        }
    }
}
