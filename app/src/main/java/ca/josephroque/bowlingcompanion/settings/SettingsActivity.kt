package ca.josephroque.bowlingcompanion.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ca.josephroque.bowlingcompanion.R

/**
 * Base activity to change user settings.
 */
class SettingsActivity : AppCompatActivity() {

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.SettingTheme)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            val preferenceFragment = SettingsFragment.newInstance()
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.pref_container, preferenceFragment)
            ft.commit()
        }
    }
}
