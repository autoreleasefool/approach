package ca.josephroque.bowlingcompanion

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.leagues.LeagueFragment
import ca.josephroque.bowlingcompanion.settings.SettingsActivity
import ca.josephroque.bowlingcompanion.utils.Email
import kotlinx.android.synthetic.main.activity_bowler_details.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to display bowler details.
 */
class BowlerDetailsActivity : AppCompatActivity() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "BowlerDetailsActivity"

        /** Intent argument for passing a [Bowler] to this activity. */
        const val INTENT_BOWLER = "${TAG}_bowler"
    }

    private var bowler: Bowler? = null

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bowler_details)

        bowler = intent.getParcelableExtra<Bowler>(INTENT_BOWLER)
        val bowler = bowler ?: return

        if (savedInstanceState == null) {
            val leagueFragment = LeagueFragment.newInstance(bowler)
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_holder, leagueFragment, LeagueFragment::class.simpleName)
                    .commit()
        }

        configureToolbar()
        configureFab()
    }

    /**
     * Configure toolbar for rendering.
     */
    private fun configureToolbar() {
        setSupportActionBar(toolbar_bowler_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Configure floating action buttons for rendering.
     */
    private fun configureFab() {
        fab?.setColorFilter(Color.BLACK)
        fab.setImageResource(R.drawable.ic_person_add_white_24dp)

        fab.setOnClickListener {
            promptAddOrEditLeague()
        }
    }

    /** @Override */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                openSettings()
                true
            }
            R.id.action_feedback -> {
                Email.sendEmail(
                        this,
                        resources.getString(R.string.feedback_email_recipient),
                        String.format(resources.getString(R.string.feedback_email_subject), BuildConfig.VERSION_CODE),
                        null
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
    private fun openSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    /**
     * Display a prompt to add or edit a league.
     *
     * @param league the league to edit, or null if a new league should be added
     */
    private fun promptAddOrEditLeague(league: League? = null) {
        TODO("Not implemented")
    }

}
