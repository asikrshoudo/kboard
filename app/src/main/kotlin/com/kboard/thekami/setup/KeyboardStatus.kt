package com.kboard.thekami.setup

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import com.kboard.thekami.ime.service.KboardImeService

/** Asks the system whether Kboard is enabled and selected, and opens the screens to fix that. */
class KeyboardStatus(private val context: Context) {
    private val inputMethods = context.getSystemService(InputMethodManager::class.java)
    private val keyboardId = android.content.ComponentName(context, KboardImeService::class.java)
        .flattenToShortString()

    fun isEnabled(): Boolean = inputMethods.enabledInputMethodList.any { it.id == keyboardId }

    fun isSelected(): Boolean =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD) == keyboardId

    fun openInputSettings() {
        context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
    }

    fun showPicker() {
        inputMethods.showInputMethodPicker()
    }
}
