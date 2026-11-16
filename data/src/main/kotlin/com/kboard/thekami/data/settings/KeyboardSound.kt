package com.kboard.thekami.data.settings

import kotlin.math.pow

/** Key sound choices. Sound is separate from haptics and is off until the user turns it on. */
data class KeyboardSound(
    val enabled: Boolean = false,
    val volumePercent: Int = DEFAULT_VOLUME_PERCENT,
) {
    /**
     * The volume to hand to the system. Android treats it as a raw gain, so the
     * slider is curved to make equal steps sound about equally different.
     */
    val gain: Float
        get() = (volumePercent.coerceIn(MIN_VOLUME_PERCENT, MAX_VOLUME_PERCENT) / PERCENT).pow(CURVE)

    companion object {
        const val MIN_VOLUME_PERCENT = 0
        const val MAX_VOLUME_PERCENT = 100
        const val VOLUME_STEP_PERCENT = 10
        const val DEFAULT_VOLUME_PERCENT = 40
        private const val PERCENT = 100f
        private const val CURVE = 2f
    }
}
