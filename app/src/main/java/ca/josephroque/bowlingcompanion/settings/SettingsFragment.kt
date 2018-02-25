package ca.josephroque.bowlingcompanion.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.utils.Email
import android.content.Intent
import android.net.Uri
import ca.josephroque.bowlingcompanion.utils.Facebook


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display user preferences and allow manipulation.
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val onPreferenceClickListener = Preference.OnPreferenceClickListener {
        when (it.key) {
            Settings.REPORT_BUG -> {
                activity?.let {
                    Email.sendEmail(
                            it,
                            resources.getString(R.string.bug_email_recipient),
                            resources.getString(R.string.bug_email_subject),
                            resources.getString(R.string.bug_email_body)
                    )
                }
                true
            }
            Settings.SEND_FEEDBACK -> {
                activity?.let {
                    Email.sendEmail(
                            it,
                            resources.getString(R.string.feedback_email_recipient),
                            resources.getString(R.string.feedback_email_subject),
                            null
                    )
                }
                true
            }
            Settings.RATE -> {
                activity?.let {
                    val appPackageName = it.packageName
                    var marketIntent: Intent
                    try {
                        marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName))
                    } catch (ex: android.content.ActivityNotFoundException) {
                        marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName))
                    }

                    startActivity(marketIntent)
                }
                true
            }
            Settings.ATTRIBUTIONS -> {
                TODO("not implemented") // Show open source attributions
            }
            Settings.FACEBOOK -> {
                context?.let { Facebook.openFacebookPage(it) }
                true
            }
             else -> false// Does nothing
        }
    }

    /** @Override */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_app);
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updatePreferenceSummaries()
    }

    /** @Override */
    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    /** @Override */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        updatePreferenceSummaries()
    }

    /**
     * Update summaries of user preferences.
     */
    private fun updatePreferenceSummaries() {
        val prefs = preferenceScreen.sharedPreferences
        findPreference(Settings.VERSION_NAME).summary = BuildConfig.VERSION_NAME

        val autoAdvanceTime = prefs.getString(Settings.AUTO_ADVANCE_TIME, resources.getString(R.string.pref_auto_advance_default))
        findPreference(Settings.AUTO_ADVANCE_TIME).summary = resources.getString(R.string.pref_auto_advance_time_summary_seconds, autoAdvanceTime)
    }

    companion object {
        /** Logging identifier. */
        private val TAG = "SettingsFragment"

        /**
         * Create a new instance of the fragment.
         *
         * @return the new instance
         */
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
