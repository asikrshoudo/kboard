package com.kboard.thekami.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.kboard.thekami.R
import com.kboard.thekami.data.settings.KeyboardAppearance
import com.kboard.thekami.engine.layout.Layouts
import kotlin.math.roundToInt

/** Slider stops between the smallest and the largest height, not counting the two ends. */
private const val HEIGHT_STEPS =
    (KeyboardAppearance.MAX_HEIGHT_PERCENT - KeyboardAppearance.MIN_HEIGHT_PERCENT) /
        KeyboardAppearance.HEIGHT_STEP_PERCENT - 1

/** Number row, key preview and keyboard height, with a drawing of the resulting keyboard. */
@Composable
fun LayoutCard(appearance: KeyboardAppearance, actions: LayoutActions) {
    // The slider moves freely while the finger drags; the choice is saved when it lifts.
    var sliderValue by remember(appearance.heightPercent) {
        mutableFloatStateOf(appearance.heightPercent.toFloat())
    }
    val percent = sliderValue.roundToInt()
    SectionCard(title = stringResource(R.string.layout_title)) {
        SwitchRow(
            title = stringResource(R.string.number_row_title),
            description = stringResource(R.string.number_row_description),
            checked = appearance.numberRowEnabled,
            onCheckedChange = actions.onNumberRowChange,
        )
        SwitchRow(
            title = stringResource(R.string.key_preview_title),
            description = stringResource(R.string.key_preview_description),
            checked = appearance.keyPreviewEnabled,
            onCheckedChange = actions.onKeyPreviewChange,
        )
        Text(text = stringResource(R.string.height_title, percent))
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { actions.onHeightChange(sliderValue.roundToInt()) },
            valueRange = KeyboardAppearance.MIN_HEIGHT_PERCENT.toFloat()..
                KeyboardAppearance.MAX_HEIGHT_PERCENT.toFloat(),
            steps = HEIGHT_STEPS,
        )
        KeyboardPreview(rows = Layouts.rowCount(appearance.numberRowEnabled), heightPercent = percent)
        Text(
            text = stringResource(R.string.layout_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
