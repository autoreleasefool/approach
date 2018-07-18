package ca.josephroque.bowlingcompanion

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.support.v7.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Application for custom methods and initialization.
 * @constructor creates a new [Application]
 */
class App : Application(), LifecycleObserver {

    /** @Override */
    override fun onCreate() {
        super.onCreate()
        PreferenceManager.setDefaultValues(this, R.xml.pref_app, false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isRunning.set(true)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isRunning.set(false)
    }

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "BowlingCompanionApp"

        /** Indicates if the app is running or not. */
        val isRunning: AtomicBoolean = AtomicBoolean(false)

        /**
         * Hides the soft keyboard with the current activity.
         *
         * @param activity the activity context
         */
        fun hideSoftKeyBoard(activity: Activity) {
            activity.currentFocus?.let {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

        /**
         * Shows the soft keyboard with the current activity.
         *
         * @param activity the activity context
         */
        fun showSoftKeyBoard(activity: Activity) {
            activity.currentFocus?.let {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(it, 0)
            }
        }
    }
}
