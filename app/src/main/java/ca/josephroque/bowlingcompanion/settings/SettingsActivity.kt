package ca.josephroque.bowlingcompanion.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import ca.josephroque.bowlingcompanion.R
import kotlinx.android.synthetic.main.activity_navigation.toolbar as toolbar

/**
 * Base activity to change user settings.
 */
class SettingsActivity : AppCompatActivity() {

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SettingTheme)
        setContentView(R.layout.activity_settings)
        setupToolbar()

        if (savedInstanceState == null) {
            val preferenceFragment = SettingsFragment.newInstance()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.pref_container, preferenceFragment)
            ft.commit()
        }
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Set up the toolbar for title and back navigation.
     */
    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setTitle(R.string.title_activity_settings)
    }
}
