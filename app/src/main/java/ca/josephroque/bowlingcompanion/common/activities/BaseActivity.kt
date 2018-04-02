package ca.josephroque.bowlingcompanion.common.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import ca.josephroque.bowlingcompanion.settings.SettingsActivity

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A base [AppCompatActivity] implementation.
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "BaseActivity"
    }

    /** @Override */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    /** @Override */
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    /**
     * Opens the settings activity.
     */
    protected fun openSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }
}