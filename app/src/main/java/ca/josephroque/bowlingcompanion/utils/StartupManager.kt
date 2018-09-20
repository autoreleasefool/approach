package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.settings.Settings


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Handle start up events.
 */
object StartupManager {

    private var preferences: SharedPreferences? = null

    var isFirstLaunch: Boolean
        get() = preferences?.getBoolean(Settings.FirstLaunch.prefName, Settings.FirstLaunch.booleanDefault) ?: Settings.FirstLaunch.booleanDefault
        set(value) { preferences?.edit()?.putBoolean(Settings.FirstLaunch.prefName, value)?.apply() }

    var appVersion: Int
        get() {
            // In previous versions, this was a string so we have to try the cast to int and return a default value if it fails
            return try {
                preferences?.getInt(Settings.AppVersion.prefName, Settings.AppVersion.intDefault) ?: Settings.AppVersion.intDefault
            } catch (ex: Exception) {
                -1
            }
        }
        set(value) { preferences?.edit()?.putInt(Settings.AppVersion.prefName, value)?.apply() }

    // MARK: StartupManager

    fun start(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val isNewVersion = BuildConfig.VERSION_CODE != appVersion
        isFirstLaunch = appVersion != Settings.AppVersion.intDefault
        appVersion = BuildConfig.VERSION_CODE

        if (isNewVersion && !isFirstLaunch) {
            Changelog.show(context)
        }

//        AppRater.appLaunched(context)
    }

    fun destroy() {
        preferences = null
    }

}
