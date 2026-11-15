package com.kboard.thekami.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.kboard.thekami.R

/** Controls for what Kboard learns: incognito mode and deleting everything learned. */
@Composable
fun LearningCard(incognito: Boolean, onIncognitoChange: (Boolean) -> Unit, onReset: () -> Unit) {
    var confirming by rememberSaveable { mutableStateOf(false) }
    SectionCard(title = stringResource(R.string.learning_title)) {
        SwitchRow(
            title = stringResource(R.string.incognito_title),
            description = stringResource(R.string.incognito_description),
            checked = incognito,
            onCheckedChange = onIncognitoChange,
        )
        Text(text = stringResource(R.string.reset_description), style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(onClick = { confirming = true }) {
            Text(stringResource(R.string.reset_title))
        }
    }
    if (confirming) {
        AlertDialog(
            onDismissRequest = { confirming = false },
            title = { Text(stringResource(R.string.reset_title)) },
            text = { Text(stringResource(R.string.reset_description)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onReset()
                        confirming = false
                    },
                ) { Text(stringResource(R.string.reset_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { confirming = false }) { Text(stringResource(R.string.reset_cancel)) }
            },
        )
    }
}
