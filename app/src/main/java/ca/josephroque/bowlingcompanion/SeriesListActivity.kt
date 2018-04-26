package ca.josephroque.bowlingcompanion

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.Menu
import ca.josephroque.bowlingcompanion.common.activities.BaseActivity
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.series.SeriesDialog
import ca.josephroque.bowlingcompanion.series.SeriesFragment
import kotlinx.android.synthetic.main.activity_series_list.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Activity to display league details.
 */
class SeriesListActivity: BaseActivity(),
        SeriesFragment.OnSeriesFragmentInteractionListener,
        SeriesDialog.OnSeriesDialogInteractionListener
{

    companion object {
        /** Logging identifier. */
        private const val TAG = "SeriesListActivity"

        /** Intent argument for passing a [League] to this activity. */
        const val INTENT_LEAGUE = "${TAG}_league"
    }

    /** The user's selected league. */
    private var league: League? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series_list)

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

    /** @Override */
    override fun onResume() {
        super.onResume()

        league?.let {
            supportActionBar?.title = it.name
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
        if (series != null) {
            val newFragment = SeriesDialog.newInstance(series)
            supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(android.R.id.content, newFragment)
                    .addToBackStack(null)
                    .commit()
        } else {
            TODO("not implemented")
        }
    }

    /**
     * Refresh the list of series.
     */
    private fun refreshSeries(series: Series? = null) {
        val fragment = supportFragmentManager.findFragmentById(R.id.layout_league_details) as? SeriesFragment ?: return
        fragment.refreshList(series)
    }

    /** @Override */
    override fun onSeriesSelected(series: Series, toEdit: Boolean) {
        if (toEdit) {
            promptAddOrEditSeries(series)
        } else {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    /** @Override */
    override fun onFinishSeries(series: Series) {
        refreshSeries(series)
    }

    /** @Override */
    override fun onDeleteSeries(series: Series) {
        val fragment = supportFragmentManager.findFragmentById(R.id.layout_league_details) as? SeriesFragment ?: return
        fragment.onItemDelete(series)
    }
}
