package com.kboard.thekami.ime.service

import android.content.Context
import android.content.Intent

/** Opens the companion app, the launcher activity of this package, where the settings are. */
internal fun Context.openCompanionApp() {
    packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
