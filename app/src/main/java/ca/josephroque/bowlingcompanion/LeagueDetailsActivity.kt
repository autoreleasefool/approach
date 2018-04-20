package ca.josephroque.bowlingcompanion

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.series.SeriesFragment
import kotlinx.android.synthetic.main.activity_league_details.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to display league details.
 */
class LeagueDetailsActivity: BaseActivity(),
        SeriesFragment.OnSeriesFragmentInteractionListener
{

    companion object {
        /** Logging identifier. */
        private const val TAG = "LeagueDetailsActivity"

        /** Intent argument for passing a [League] to this activity. */
        const val INTENT_LEAGUE = "${TAG}_league"
    }

    /** The user's selected league. */
    private var league: League? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_league_details)

        league = intent.getParcelableExtra(INTENT_LEAGUE)
        val league = league ?: return

        configureToolbar()
        configureFab()

        if (savedInstanceState == null) {
            val fragment = SeriesFragment.newInstance(league)
            supportFragmentManager.beginTransaction()
                    .add(R.id.layout_league_details, fragment, null)
                    .commit()
        }
    }

    /**
     * Configure toolbar for rendering.
     */
    private fun configureToolbar() {
        setSupportActionBar(toolbar_league_details)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    /**
     * Configure floating action buttons for rendering.
     */
    private fun configureFab() {
        fab?.setColorFilter(Color.BLACK)
        fab.setImageResource(R.drawable.ic_add_white_24dp)

        fab.setOnClickListener {
            promptAddOrEditSeries()
        }
    }

    /** @Override */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_activity_series, menu)
        return true
    }

    /**
     * Display a prompt to add or edit a series.
     *
     * @param series the series to edit, or null if a new series should be added
     */
    private fun promptAddOrEditSeries(series: Series? = null) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** @Override */
    override fun onSeriesSelected(series: Series, toEdit: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
