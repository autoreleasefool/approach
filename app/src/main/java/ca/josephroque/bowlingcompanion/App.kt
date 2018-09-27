package ca.josephroque.bowlingcompanion

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.support.v7.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import com.bugsnag.android.Bugsnag
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Application for custom methods and initialization.
 * @constructor creates a new [Application]
 */
class App : Application(), LifecycleObserver {

    companion object {
        @Suppress("unused")
        private const val TAG = "BowlingCompanionApp"

        val isRunning: AtomicBoolean = AtomicBoolean(false)

        fun hideSoftKeyBoard(activity: Activity) {
            activity.currentFocus?.let {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

        fun showSoftKeyBoard(activity: Activity) {
            activity.currentFocus?.let {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(it, 0)
            }
        }
    }

    // MARK: Lifecycle functions

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.setDefaultValues(this, R.xml.pref_app, false)
        MobileAds.initialize(this, BuildConfig.BANNER_AD_UNIT_ID)
        Bugsnag.init(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isRunning.set(false)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isRunning.set(true)
    }
}
