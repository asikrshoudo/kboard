package com.kboard.thekami.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.kboard.thekami.R

private const val KEYS_PER_ROW = 10
private const val FULL_PERCENT = 100f

/**
 * A drawing of the keyboard's key grid: [rows] rows, as tall as the keyboard
 * would be at [heightPercent]. It shows the size and the number row at a glance;
 * the real keyboard changes once the slider is released.
 */
@Composable
fun KeyboardPreview(rows: Int, heightPercent: Int) {
    val keyHeight = dimensionResource(R.dimen.preview_key_height) * (heightPercent / FULL_PERCENT)
    val gap = dimensionResource(R.dimen.preview_key_gap)
    val keyShape = RoundedCornerShape(dimensionResource(R.dimen.preview_key_radius))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(dimensionResource(R.dimen.preview_radius)),
            )
            .padding(gap),
    ) {
        repeat(rows) {
            Row(modifier = Modifier.fillMaxWidth().height(keyHeight)) {
                repeat(KEYS_PER_ROW) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(gap / 2)
                            .background(color = MaterialTheme.colorScheme.surface, shape = keyShape),
                    )
                }
            }
        }
    }
}
