package com.kboard.thekami

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Hosts the Hilt dependency graph shared by the
 * companion app and the keyboard service.
 */
@HiltAndroidApp
class KboardApp : Application()
