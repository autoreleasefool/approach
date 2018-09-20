package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.support.v7.preference.PreferenceManager
import ca.josephroque.bowlingcompanion.BuildConfig

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Handle start up events.
 */
object StartupManager {

    private const val APP_VERSION = "pref_version"
    private const val IS_FIRST_LAUNCH = "is_first_launch"

    // MARK: StartupManager

    fun start(context: Context) {
        val appVersion = getAppVersion(context)
        val isNewVersion = BuildConfig.VERSION_CODE != appVersion
        setIsFirstLaunch(context, appVersion != -1)
        setAppVersion(context, BuildConfig.VERSION_CODE)
        val isFirstLaunch = isFirstLaunch(context)

        if (isNewVersion && !isFirstLaunch) {
            Changelog.show(context)
        }

        AppRater.prepare(context)
    }

    fun isFirstLaunch(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(IS_FIRST_LAUNCH, false)
    }

    fun getAppVersion(context: Context): Int {
        val defaultValue = -1
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        // In previous versions, this was a string so we have to try the cast to int and return a default value if it fails
        return try {
            preferences.getInt(APP_VERSION, defaultValue)
        } catch (ex: Exception) {
            defaultValue
        }
    }

    // MARK: Private functions

    private fun setIsFirstLaunch(context: Context, isFirstLaunch: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putBoolean(IS_FIRST_LAUNCH, isFirstLaunch).apply()
    }

    private fun setAppVersion(context: Context, appVersion: Int) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().putInt(APP_VERSION, appVersion).apply()
    }
}
