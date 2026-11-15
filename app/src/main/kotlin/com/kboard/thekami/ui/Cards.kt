package com.kboard.thekami.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kboard.thekami.R

@Composable
fun TypingCard(hapticsEnabled: Boolean, onHapticsChange: (Boolean) -> Unit) {
    SectionCard(title = stringResource(R.string.typing_title)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium)),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.haptics_title))
                Text(
                    text = stringResource(R.string.haptics_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = hapticsEnabled, onCheckedChange = onHapticsChange)
        }
    }
}

/** A text field for trying the keyboard without leaving the app. */
@Composable
fun TryItCard() {
    var text by rememberSaveable { mutableStateOf("") }
    SectionCard(title = stringResource(R.string.try_title)) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(stringResource(R.string.try_label)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun PrivacyCard() {
    SectionCard(title = stringResource(R.string.privacy_title)) {
        Text(text = stringResource(R.string.privacy_body), style = MaterialTheme.typography.bodyMedium)
    }
}
