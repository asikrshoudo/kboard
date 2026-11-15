package com.kboard.thekami.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kboard.thekami.R

private enum class StepState { DONE, ACTIVE, LOCKED }

/** Guides the user through enabling and selecting Kboard, then confirms it is ready. */
@Composable
fun SetupCard(enabled: Boolean, selected: Boolean, onEnable: () -> Unit, onSelect: () -> Unit) {
    val ready = enabled && selected
    val title = if (ready) R.string.ready_title else R.string.setup_title
    SectionCard(title = stringResource(title)) {
        if (ready) {
            Text(stringResource(R.string.ready_body))
        } else {
            StepRow(
                title = stringResource(R.string.step_enable_title),
                description = stringResource(R.string.step_enable_description),
                action = stringResource(R.string.step_enable_action),
                state = if (enabled) StepState.DONE else StepState.ACTIVE,
                onClick = onEnable,
            )
            StepRow(
                title = stringResource(R.string.step_select_title),
                description = stringResource(R.string.step_select_description),
                action = stringResource(R.string.step_select_action),
                state = if (enabled) StepState.ACTIVE else StepState.LOCKED,
                onClick = onSelect,
            )
        }
    }
}

@Composable
private fun StepRow(title: String, description: String, action: String, state: StepState, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Text(text = description, style = MaterialTheme.typography.bodyMedium)
        if (state == StepState.DONE) {
            Text(text = stringResource(R.string.step_done), color = MaterialTheme.colorScheme.primary)
        } else {
            FilledTonalButton(onClick = onClick, enabled = state == StepState.ACTIVE) {
                Text(action)
            }
        }
    }
}
