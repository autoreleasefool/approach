package ca.josephroque.bowlingcompanion.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.ScrollableTextDialog
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.Email
import ca.josephroque.bowlingcompanion.utils.Facebook
import ca.josephroque.bowlingcompanion.utils.Files
import ca.josephroque.bowlingcompanion.utils.toSpanned

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display user preferences and allow manipulation.
 */
class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        @Suppress("unused")
        private const val TAG = "SettingsFragment"

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    private val onPreferenceChangedListener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
        when (key) {
            Settings.BooleanSetting.EnableAutoAdvance.prefName -> {
                val enabled = Settings.BooleanSetting.EnableAutoAdvance.getValue(preferences)
                if (enabled) {
                    Analytics.trackEnableAutoAdvance()
                } else {
                    Analytics.trackDisableAutoAdvance()
                }
            }
            Settings.BooleanSetting.EnableAutoLock.prefName -> {
                val enabled = Settings.BooleanSetting.EnableAutoLock.getValue(preferences)
                if (enabled) {
                    Analytics.trackEnableAutoLock()
                } else {
                    Analytics.trackDisableAutoLock()
                }
            }
            else -> {} // Do nothing
        }
    }

    private val onPreferenceClickListener = Preference.OnPreferenceClickListener {
        when (it.key) {
            Settings.StaticSetting.ReportBug.prefName -> {
                sendBugReportEmail()
                true
            }
            Settings.StaticSetting.SendFeedback.prefName -> {
                sendFeedbackEmail()
                true
            }
            Settings.StaticSetting.Rate.prefName -> {
                displayPlayStoreListing()
                true
            }
            Settings.StaticSetting.Attributions.prefName -> {
                displayAttributions()
                true
            }
            Settings.StaticSetting.Facebook.prefName -> {
                displayFacebookPage()
                true
            }
            Settings.StaticSetting.DeveloperWebsite.prefName -> {
                Analytics.trackViewWebsite()
                true
            }
            Settings.StaticSetting.ViewSource.prefName -> {
                Analytics.trackViewSource()
                true
            }
            Settings.StaticSetting.PrivacyPolicy.prefName -> {
                Analytics.trackViewPrivacyPolicy()
                true
            }
            else -> false // Does nothing
        }
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(onPreferenceChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(onPreferenceChangedListener)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_app)

        findPreference(Settings.StaticSetting.ReportBug.prefName).onPreferenceClickListener = onPreferenceClickListener
        findPreference(Settings.StaticSetting.SendFeedback.prefName).onPreferenceClickListener = onPreferenceClickListener
        findPreference(Settings.StaticSetting.Rate.prefName).onPreferenceClickListener = onPreferenceClickListener
        findPreference(Settings.StaticSetting.Attributions.prefName).onPreferenceClickListener = onPreferenceClickListener
        findPreference(Settings.StaticSetting.Facebook.prefName).onPreferenceClickListener = onPreferenceClickListener
        findPreference(Settings.StaticSetting.DeveloperWebsite.prefName).onPreferenceClickListener = onPreferenceClickListener
        findPreference(Settings.StaticSetting.ViewSource.prefName).onPreferenceClickListener = onPreferenceClickListener
    }

    override fun onStart() {
        super.onStart()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        updatePreferenceSummaries()
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        updatePreferenceSummaries()
    }

    // MARK: Private function

    private fun sendBugReportEmail() {
        val emailName = resources.getString(R.string.feedback_email_recipient_name);
        val emailDomain = resources.getString(R.string.feedback_email_recipient_domain);
        val emailTld = resources.getString(R.string.feedback_email_recipient_tld);

        activity?.let {
            Email.sendEmail(
                    it,
                    "$emailName@$emailDomain.$emailTld",
                    resources.getString(R.string.bug_email_subject),
                    resources.getString(R.string.bug_email_body)
            )
        }

        Analytics.trackReportBug()
    }

    private fun sendFeedbackEmail() {
        val emailName = resources.getString(R.string.feedback_email_recipient_name);
        val emailDomain = resources.getString(R.string.feedback_email_recipient_domain);
        val emailTld = resources.getString(R.string.feedback_email_recipient_tld);

        activity?.let {
            Email.sendEmail(
                    it,
                    "$emailName@$emailDomain.$emailTld",
                    resources.getString(R.string.feedback_email_subject),
                    null
            )
        }

        Analytics.trackSendFeedback()
    }

    private fun displayPlayStoreListing() {
        activity?.let {
            val appPackageName = it.packageName
            val marketIntent = try {
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            } catch (ex: android.content.ActivityNotFoundException) {
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
            }

            startActivity(marketIntent)
        }

        Analytics.trackRate()
    }

    private fun displayAttributions() {
        activity?.let { activity ->
            val licenseText = Files.retrieveTextFileAsset(activity, "licenses.txt")

            licenseText?.let {
                val fragment = ScrollableTextDialog.newInstance(
                        R.string.open_source_libraries,
                        it.replace("\n", "<br />").toSpanned()
                )

                activity.supportFragmentManager.beginTransaction().apply {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    add(android.R.id.content, fragment)
                    addToBackStack(null)
                    commit()
                }
            }
        }

        Analytics.trackViewAttributions()
    }

    private fun displayFacebookPage() {
        activity?.let {
            it.startActivity(Facebook.getFacebookPageIntent(it))
        }

        Analytics.trackViewFacebook()
    }

    private fun updatePreferenceSummaries() {
        val prefs = preferenceScreen.sharedPreferences
        findPreference(Settings.StaticSetting.VersionName.prefName).summary = BuildConfig.VERSION_NAME

        val autoAdvanceTime = Settings.StringSetting.AutoAdvanceTime.getValue(prefs)
        val timeComponents = autoAdvanceTime.split(" ")
        findPreference(Settings.StringSetting.AutoAdvanceTime.prefName).summary = resources.getString(R.string.pref_auto_advance_time_summary_seconds, timeComponents[0])
    }
}
