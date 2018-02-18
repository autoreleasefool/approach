package ca.josephroque.bowlingcompanion

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * Copyright (C) 2018 Joseph Roque
 */
class App : Application() {
    companion object {
        fun hideSoftKeyBoard(activity: Activity) {
            val view = activity.currentFocus
            if (view != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}