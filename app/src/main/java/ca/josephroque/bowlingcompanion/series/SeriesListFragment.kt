package ca.josephroque.bowlingcompanion.series

import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.view.*
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.settings.Settings
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of series.
 */
class SeriesListFragment : ListFragment<Series, SeriesRecyclerViewAdapter.ViewHolder, SeriesRecyclerViewAdapter>(),
        SeriesDialog.OnSeriesDialogInteractionListener,
        IFloatingActionButtonHandler {

    companion object {
        /** Logging identifier. */
        private const val TAG = "SeriesListFragment"

        /** Identifier for the argument that represents the [League] whose series are displayed. */
        private const val ARG_LEAGUE = "${TAG}_league"

        fun newInstance(league: League): SeriesListFragment {
            val fragment = SeriesListFragment()
            val args = Bundle()
            args.putParcelable(ARG_LEAGUE, league)
            fragment.arguments = args
            return fragment
        }
    }

    /** The league whose series are to be displayed. */
    private var league: League? = null

    /** Indicates how to render series in the list. */
    private var seriesView: Series.Companion.View = Series.Companion.View.Expanded
        set(value) {
            context?.let {
                PreferenceManager.getDefaultSharedPreferences(it)
                        .edit()
                        .putInt(Series.PREFERRED_VIEW, value.ordinal)
                        .apply()
            }

            activity?.invalidateOptionsMenu()
            adapter?.seriesView = value
            field = value
        }

    /** @Override. */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        league = savedInstanceState?.getParcelable(ARG_LEAGUE) ?: arguments?.getParcelable(ARG_LEAGUE)
        context?.let {
            seriesView = Series.Companion.View.fromInt(PreferenceManager.getDefaultSharedPreferences(it)
                    .getInt(Series.PREFERRED_VIEW, Series.Companion.View.Expanded.ordinal))!!
        }

        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override. */
    override fun onResume() {
        super.onResume()
        val context = context ?: return
        val league = league ?: return
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

        val shouldHighlightSeries = preferenceManager.getBoolean(Settings.HIGHLIGHT_SERIES_ENABLED, true)
        val shouldHighlightScores = preferenceManager.getBoolean(Settings.HIGHLIGHT_SCORE_ENABLED, true)

        adapter?.gameHighlightMin = league.gameHighlight
        adapter?.seriesHighlightMin = league.seriesHighlight
        adapter?.shouldHighlightSeries = shouldHighlightSeries
        adapter?.shouldHighlightScores = shouldHighlightScores
    }

    /** @Override. */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_series, menu)

        when (seriesView) {
            Series.Companion.View.Expanded -> {
                menu.findItem(R.id.action_series_expanded_view).isVisible = false
                menu.findItem(R.id.action_series_condensed_view).isVisible = true
            }
            Series.Companion.View.Condensed -> {
                menu.findItem(R.id.action_series_expanded_view).isVisible = true
                menu.findItem(R.id.action_series_condensed_view).isVisible = false
            }
        }
    }

    /** @Override */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_stats -> {
                TODO("league stats not implemented")
            }
            R.id.action_series_condensed_view, R.id.action_series_expanded_view -> {
                val view = if (item.itemId == R.id.action_series_expanded_view) Series.Companion.View.Expanded else Series.Companion.View.Condensed
                seriesView = view
                return true
            }
            else -> false
        }
    }

    /** @Override */
    override fun buildAdapter(): SeriesRecyclerViewAdapter {
        val adapter = SeriesRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = true
        adapter.seriesView = seriesView
        return adapter
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<Series>> {
        return async(CommonPool) {
            this@SeriesListFragment.context?.let { context ->
                league?.let {
                    return@async it.fetchSeries(context).await()
                }
            }

            emptyList<Series>().toMutableList()
        }
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return R.drawable.ic_add_white_24dp
    }

    /** @Override */
    override fun onFabClick() {
        promptAddOrEditSeries()
    }

    /** @Override */
    override fun onFinishSeries(series: Series) {
        refreshList(series)
    }

    /** @Override */
    override fun onDeleteSeries(series: Series) {
        onItemDelete(series)
    }

    /**
     * Display a prompt to add or edit a series.
     *
     * @param series the series to edit, or null if a new series should be added
     */
    private fun promptAddOrEditSeries(series: Series? = null) {
        if (series != null) {
            val newFragment = SeriesDialog.newInstance(series)
            fragmentNavigation?.pushDialogFragment(newFragment)
        } else {
            TODO("not implemented")
        }
    }
}