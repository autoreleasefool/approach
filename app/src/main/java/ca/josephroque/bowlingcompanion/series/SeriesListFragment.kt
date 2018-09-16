package ca.josephroque.bowlingcompanion.series

import android.content.Context
import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.interfaces.IFloatingActionButtonHandler
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.games.GameControllerFragment
import ca.josephroque.bowlingcompanion.games.SeriesProvider
import ca.josephroque.bowlingcompanion.leagues.League
import ca.josephroque.bowlingcompanion.settings.Settings
import ca.josephroque.bowlingcompanion.statistics.interfaces.IStatisticsContext
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of series.
 */
class SeriesListFragment : ListFragment<Series, SeriesRecyclerViewAdapter>(),
        SeriesDialog.SeriesDialogDelegate,
        ListFragment.ListFragmentDelegate,
        IFloatingActionButtonHandler,
        IStatisticsContext {

    companion object {
        @Suppress("unused")
        private const val TAG = "SeriesListFragment"

        private const val ARG_LEAGUE = "${TAG}_league"
        private const val ARG_SINGLE_SELECT_MODE = "${TAG}_single_select"

        fun newInstance(league: League, singleSelectMode: Boolean = false): SeriesListFragment {
            val fragment = SeriesListFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(ARG_LEAGUE, league)
                putBoolean(ARG_SINGLE_SELECT_MODE, singleSelectMode)
            }
            return fragment
        }
    }

    override val emptyViewImage = R.drawable.empty_view_series
    override val emptyViewText = R.string.empty_view_series

    override val statisticsProviders: List<StatisticsProvider> by lazy {
        val league = league
        return@lazy if (league != null) {
            arrayListOf(
                StatisticsProvider.BowlerStatistics(league.bowler),
                StatisticsProvider.LeagueStatistics(league)
            )
        } else {
            emptyList<StatisticsProvider>()
        }
    }

    private var league: League? = null
    private var singleSelectMode: Boolean = false

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

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            league = it.getParcelable(ARG_LEAGUE)
            singleSelectMode = it.getBoolean(ARG_SINGLE_SELECT_MODE)
        }
        context?.let {
            seriesView = Series.Companion.View.fromInt(PreferenceManager.getDefaultSharedPreferences(it)
                    .getInt(Series.PREFERRED_VIEW, Series.Companion.View.Expanded.ordinal))!!
        }

        setHasOptionsMenu(!singleSelectMode)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        canIgnoreDelegate = true
        delegate = this
        super.onAttach(context)
        singleSelectMode = arguments?.getBoolean(ARG_SINGLE_SELECT_MODE) ?: false
        if (singleSelectMode) {
            val parent = parentFragment as? ListFragmentDelegate ?: return
            delegate = parent
        }
    }

    override fun onStart() {
        super.onStart()
        val context = context ?: return
        val league = league ?: return

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
        val shouldHighlightSeries = preferenceManager.getBoolean(Settings.HighlightSeriesEnabled.prefName, Settings.HighlightSeriesEnabled.booleanDefault)
        val shouldHighlightScores = preferenceManager.getBoolean(Settings.HighlightScoreEnabled.prefName, Settings.HighlightScoreEnabled.booleanDefault)

        adapter?.let {
            it.gameHighlightMin = league.gameHighlight
            it.seriesHighlightMin = league.seriesHighlight
            it.shouldHighlightSeries = shouldHighlightSeries
            it.shouldHighlightScores = shouldHighlightScores
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_series, menu)

        when (seriesView) {
            Series.Companion.View.Expanded -> {
                menu.findItem(R.id.action_series_expanded_view).isVisible = false
                menu.findItem(R.id.action_series_condensed_view).isVisible = true

                Analytics.trackToggledSeriesView(Series.Companion.View.Expanded)
            }
            Series.Companion.View.Condensed -> {
                menu.findItem(R.id.action_series_expanded_view).isVisible = true
                menu.findItem(R.id.action_series_condensed_view).isVisible = false

                Analytics.trackToggledSeriesView(Series.Companion.View.Condensed)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_series_condensed_view, R.id.action_series_expanded_view -> {
                val view = if (item.itemId == R.id.action_series_expanded_view) Series.Companion.View.Expanded else Series.Companion.View.Condensed
                seriesView = view
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        if (!singleSelectMode) {
            league?.let { navigationActivity?.setToolbarTitle(it.bowler.name, it.name) }
        }
    }

    // MARK: ListFragment

    override fun buildAdapter(): SeriesRecyclerViewAdapter {
        val adapter = SeriesRecyclerViewAdapter(emptyList(), this)
        adapter.swipeable = !singleSelectMode
        adapter.longPressable = !singleSelectMode
        adapter.seriesView = seriesView
        return adapter
    }

    override fun fetchItems(): Deferred<MutableList<Series>> {
        return async(CommonPool) {
            this@SeriesListFragment.context?.let { context ->
                league?.let {
                    return@async it.fetchSeries(context).await()
                }
            }

            mutableListOf<Series>()
        }
    }

    // MARK: IFloatingActionButtonHandler

    override fun getFabImage(): Int? {
        return R.drawable.ic_add
    }

    override fun onFabClick() {
        promptAddOrEditSeries()
    }

    // MARK: SeriesDialogDelegate

    override fun onFinishSeries(series: Series) {
        refreshList(series)
    }

    override fun onDeleteSeries(series: Series) {
        onItemDelete(series)
    }

    // MARK: AdapterDelegate

    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is Series) {
            if (longPress) {
                promptAddOrEditSeries(item)
            } else {
                showGameDetails(item)

                Analytics.trackSelectSeries()
            }
        }
    }

    // MARK: Private functions

    private fun promptAddOrEditSeries(series: Series? = null) {
        if (series != null) {
            val newFragment = SeriesDialog.newInstance(series)
            fragmentNavigation?.pushDialogFragment(newFragment)
        } else {
            safeLet(context, league) { context, league ->
                launch(Android) {
                    val (newSeries, seriesError) = league.createNewSeries(context).await()
                    if (seriesError != null) {
                        seriesError.show(context)
                    } else if (newSeries != null) {
                        showGameDetails(newSeries)

                        Analytics.trackCreateSeries()
                    }
                }
            }
        }
    }

    private fun showGameDetails(series: Series) {
        val newFragment = GameControllerFragment.newInstance(SeriesProvider.BowlerSeries(series))
        fragmentNavigation?.pushFragment(newFragment)

        Analytics.trackSelectSeries()
    }
}
