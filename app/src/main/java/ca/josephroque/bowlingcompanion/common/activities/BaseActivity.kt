package ca.josephroque.bowlingcompanion.common.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import ca.josephroque.bowlingcompanion.BuildConfig
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.settings.SettingsActivity
import ca.josephroque.bowlingcompanion.utils.Email

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A base [AppCompatActivity] implementation.
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        /** Logging identifier. */
        private val TAG = BaseActivity::class.simpleName
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                openSettings()
                true
            }
            R.id.action_feedback -> {
                prepareFeedbackEmail()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Prepare an email with the subject and recipient pre-filled.
     */
    private fun prepareFeedbackEmail() {
        Email.sendEmail(
                this,
                resources.getString(R.string.feedback_email_recipient),
                String.format(resources.getString(R.string.feedback_email_subject), BuildConfig.VERSION_CODE),
                null
        )
    }

    /**
     * Opens the settings activity.
     */
    private fun openSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }
}