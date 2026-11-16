package com.kboard.thekami.ui

import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.kboard.thekami.R
import com.kboard.thekami.data.settings.KeyboardSound
import kotlin.math.roundToInt

/** Slider stops between the quietest and the loudest setting, not counting the two ends. */
private const val VOLUME_STEPS =
    (KeyboardSound.MAX_VOLUME_PERCENT - KeyboardSound.MIN_VOLUME_PERCENT) / KeyboardSound.VOLUME_STEP_PERCENT - 1

private val VOLUME_RANGE = KeyboardSound.MIN_VOLUME_PERCENT.toFloat()..KeyboardSound.MAX_VOLUME_PERCENT.toFloat()

/** Key sound on or off, and its volume. Independent of the vibration setting. */
@Composable
fun SoundCard(sound: KeyboardSound, actions: SoundActions) {
    // The slider moves freely while the finger drags; the choice is saved when it lifts.
    var sliderValue by remember(sound.volumePercent) { mutableFloatStateOf(sound.volumePercent.toFloat()) }
    SectionCard(title = stringResource(R.string.sound_title)) {
        SwitchRow(
            title = stringResource(R.string.sound_enabled_title),
            description = stringResource(R.string.sound_enabled_description),
            checked = sound.enabled,
            onCheckedChange = actions.onEnabledChange,
        )
        Text(text = stringResource(R.string.volume_title, sliderValue.roundToInt()))
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { actions.onVolumeChange(sliderValue.roundToInt()) },
            valueRange = VOLUME_RANGE,
            steps = VOLUME_STEPS,
            enabled = sound.enabled,
        )
    }
}
