package com.kboard.thekami.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.kboard.thekami.R
import com.kboard.thekami.data.settings.SpaceLongPress

private fun labelOf(option: SpaceLongPress): Int = when (option) {
    SpaceLongPress.SWITCH_LANGUAGE -> R.string.space_long_press_language
    SpaceLongPress.KEYBOARD_PICKER -> R.string.space_long_press_picker
    SpaceLongPress.NOTHING -> R.string.space_long_press_nothing
}

/** What holding the space bar does. */
@Composable
fun GesturesCard(spaceLongPress: SpaceLongPress, actions: GestureActions) {
    SectionCard(title = stringResource(R.string.gestures_title)) {
        Text(text = stringResource(R.string.space_long_press_title))
        SpaceLongPress.entries.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = option == spaceLongPress,
                        role = Role.RadioButton,
                        onClick = { actions.onSpaceLongPressChange(option) },
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = option == spaceLongPress, onClick = null)
                Text(
                    text = stringResource(labelOf(option)),
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small)),
                )
            }
        }
        Text(
            text = stringResource(R.string.space_long_press_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
