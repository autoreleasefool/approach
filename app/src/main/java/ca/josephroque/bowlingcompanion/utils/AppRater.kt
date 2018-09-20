package ca.josephroque.bowlingcompanion.utils

import android.content.Context
import android.support.v7.preference.PreferenceManager
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.R

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Prompt the user to rate the app in the app store.
 */
object AppRater {

    private const val MINIMUM_DAYS_UNTIL_PROMPT = 14
    private const val MINIMUM_LAUNCHES_UNTIL_PROMPT = 3
    private const val DAYS_BETWEEN_PROMPTS = 14

    private const val MINIMUM_MILLISECONDS_UNTIL_PROMPT = MINIMUM_DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000
    private const val MILLISECONDS_BETWEEN_PROMPTS = DAYS_BETWEEN_PROMPTS * 24 * 60 * 60 * 1000

    private const val LAUNCH_COUNT = "launch_count"
    private const val LAST_PROMPT_TIME = "app_rater_last_prompt_time"
    private const val DISABLE_APP_RATER = "disable_app_rater"
    private const val TIME_OF_FIRST_LAUNCH = "time_of_first_launch"
    private const val CAN_SHOW_APP_RATER = "can_show_app_rater"

    // MARK: AppRater

    fun prepare(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        if (preferences.getBoolean(DISABLE_APP_RATER, false)) {
            return
        }

        val preferencesEditor = preferences.edit()

        val launchCount = preferences.getLong(LAUNCH_COUNT, 0) + 1
        preferencesEditor.putLong(LAUNCH_COUNT, launchCount)

        val timeOfFirstLaunch = if (StartupManager.isFirstLaunch(context)) {
            System.currentTimeMillis()
        } else {
            preferences.getLong(TIME_OF_FIRST_LAUNCH, System.currentTimeMillis())
        }
        preferencesEditor.putLong(TIME_OF_FIRST_LAUNCH, System.currentTimeMillis())

        var dateToWaitFor = timeOfFirstLaunch + MINIMUM_MILLISECONDS_UNTIL_PROMPT

        val lastPromptTime = preferences.getLong(LAST_PROMPT_TIME, 0)
        if (lastPromptTime != 0L) {
            dateToWaitFor = lastPromptTime + MILLISECONDS_BETWEEN_PROMPTS
        }

        if (launchCount > MINIMUM_LAUNCHES_UNTIL_PROMPT && System.currentTimeMillis() > dateToWaitFor) {
            preferencesEditor.putBoolean(CAN_SHOW_APP_RATER, true)
        } else {
            preferencesEditor.putBoolean(CAN_SHOW_APP_RATER, false)
        }

        preferencesEditor.apply()
    }

    fun show(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (!preferences.getBoolean(CAN_SHOW_APP_RATER, false)) {
            return
        }

        preferences.edit().putLong(LAST_PROMPT_TIME, System.currentTimeMillis()).apply()

        val dialog = AlertDialog.Builder(context)
        val rootView = View.inflate(context, R.layout.dialog_app_rater, null)
        dialog.setView(rootView)
        val alertDialog = dialog.create()

        val listener = View.OnClickListener {
            when (it.id) {
                R.id.btn_rate_remind_me -> {
                    Analytics.trackAppRateDialogIgnore()
                }
                R.id.btn_rate -> {
                    Analytics.trackAppRateDialogRate()
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")))
                    } catch (ex: android.content.ActivityNotFoundException) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")))
                    }

                    disableAppRater(context)
                }
                R.id.btn_rate_no -> {
                    Analytics.trackAppRateDialogDisable()
                    disableAppRater(context)
                }
            }

            alertDialog.dismiss()
        }

        rootView.findViewById<Button>(R.id.btn_rate).setOnClickListener(listener)
        rootView.findViewById<Button>(R.id.btn_rate_no).setOnClickListener(listener)
        rootView.findViewById<Button>(R.id.btn_rate_remind_me).setOnClickListener(listener)

        alertDialog.show()

        Analytics.trackViewAppRateDialog()
    }

    // MARK: Private functions

    private fun disableAppRater(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putBoolean(CAN_SHOW_APP_RATER, false)
            putBoolean(DISABLE_APP_RATER, true)
            apply()
        }
    }
}
