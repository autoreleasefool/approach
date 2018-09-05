package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.NavigationDrawerController
import ca.josephroque.bowlingcompanion.common.adapters.BaseFragmentPagerAdapter
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.common.interfaces.INavigationDrawerHandler
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.statistics.interfaces.IStatisticsContext
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import ca.josephroque.bowlingcompanion.utils.Analytics
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_pager as fragmentPager
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_tabs as fragmentTabs

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Manage tabs to show games for the team of bowlers.
 */
class GameControllerFragment : TabbedFragment(),
        INavigationDrawerHandler,
        GameFragment.GameFragmentDelegate,
        IStatisticsContext {

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "GameControllerFragment"

        /** Argument identifier for passing a [SeriesProvider] type. */
        private const val ARG_SERIES_PROVIDER_TYPE = "${TAG}_type"

        /** Argument identifier for passing a [SeriesProvider] to this fragment. */
        private const val ARG_SERIES_PROVIDER = "${TAG}_series"

        /**
         * Creates a new instance.
         *
         * @param seriesProvider the series to edit games for
         * @return the new instance
         */
        fun newInstance(seriesProvider: SeriesProvider): GameControllerFragment {
            val fragment = GameControllerFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_SERIES_PROVIDER_TYPE, seriesProvider.describeContents())
                putParcelable(ARG_SERIES_PROVIDER, seriesProvider)
            }
            return fragment
        }
    }

    /** @Override */
    override val statisticsProviders: List<StatisticsProvider> by lazy {
        val seriesProvider = seriesProvider
        val seriesList = seriesProvider?.seriesList
        val adapter = fragmentPager.adapter as? GameControllerPagerAdapter
        val gameFragment = adapter?.getFragment(currentTab) as? GameFragment

        return@lazy if (seriesList != null && gameFragment != null) {
            val providers: MutableList<StatisticsProvider> = arrayListOf(
                StatisticsProvider.BowlerStatistics(seriesList[currentTab].league.bowler),
                StatisticsProvider.LeagueStatistics(seriesList[currentTab].league),
                StatisticsProvider.SeriesStatistics(seriesList[currentTab]),
                StatisticsProvider.GameStatistics(gameFragment.currentGameForStatistics)
            )

            if (seriesProvider is SeriesProvider.TeamSeries) {
                providers.add(0, StatisticsProvider.TeamStatistics(seriesProvider.team))
            }

            providers
        } else {
            emptyList<StatisticsProvider>()
        }
    }

    /** Controls for the navigation drawer. */
    override lateinit var navigationDrawerController: NavigationDrawerController

    /** The series being edited. */
    private var seriesProvider: SeriesProvider? = null

    /** The current game being edited. */
    private var currentGame: Int = 0
        set(value) {
            field = value

            val adapter = fragmentPager.adapter as? GameControllerPagerAdapter
            val gameFragment = adapter?.getFragment(currentTab) as? GameFragment
            gameFragment?.gameNumber = currentGame

            navigationDrawerController.gameNumber = currentGame

            Analytics.trackChangedGame()
        }

    /** Indicates if the floating action button should be enabled or not. */
    private var fabEnabled: Boolean = true

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val seriesType = arguments?.getInt(ARG_SERIES_PROVIDER_TYPE) ?: 0
        seriesProvider = SeriesProvider.getParcelable(arguments, ARG_SERIES_PROVIDER, seriesType)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return GameControllerPagerAdapter(childFragmentManager, tabCount, seriesProvider?.seriesList)
    }

    /** @Override */
    override fun addTabs(tabLayout: TabLayout) {
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        seriesProvider?.seriesList?.let {
            it.forEach { series ->
                tabLayout.addTab(tabLayout.newTab().setText(series.league.bowler.name))
            }
        }
    }

    /** @Override */
    override fun handleTabSwitch(newTab: Int) {
        onSeriesChanged()
    }

    /** @Override */
    override fun onStart() {
        super.onStart()
        val seriesList = seriesProvider?.seriesList ?: return
        if (seriesList.size == 1) {
            fragmentTabs.visibility = View.GONE
            navigationActivity?.supportActionBar?.elevation = resources.getDimension(R.dimen.base_elevation)
        } else {
            fragmentTabs.visibility = View.VISIBLE
            navigationActivity?.supportActionBar?.elevation = 0F
        }
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        onSeriesChanged()
        activity?.invalidateOptionsMenu()
        navigationDrawerController.isTeamMember = seriesProvider is SeriesProvider.TeamSeries
    }

    /**
     * Handle when series changes.
     */
    private fun onSeriesChanged() {
        seriesProvider?.seriesList?.let {
            navigationDrawerController.numberOfGames = it[currentTab].numberOfGames
            navigationDrawerController.bowlerName = it[currentTab].league.bowler.name
            navigationDrawerController.leagueName = it[currentTab].league.name
        }

        updateToolbarTitle()
    }

    /** @Override */
    override fun updateToolbarTitle() {
        seriesProvider?.seriesList?.let { navigationActivity?.setToolbarTitle(it[currentTab].league.bowler.name) }
    }

    // MARK: IFloatingActionButtonHandler

    /** @Override */
    override fun getFabImage(): Int? {
        return if (fabEnabled) R.drawable.ic_arrow_forward else null
    }

    /** @Override */
    override fun onFabClick() {
        if (!fabEnabled) {
            return
        }

        val adapter = fragmentPager?.adapter as? GameControllerPagerAdapter
        val gameFragment = adapter?.getFragment(currentTab) as? GameFragment
        gameFragment?.onFabClick()
    }

    // MARK: INavigationDrawerHandler

    /** @Override */
    override fun onNavDrawerItemSelected(@IdRes itemId: Int) {
        val game = NavigationDrawerController.navGameItemIds.indexOf(itemId)
        if (game >= 0) {
            currentGame = game
        }
    }

    // MARK: GameFragmentDelegate

    /** @Override */
    override val isFabEnabled: Boolean
        get() = fabEnabled

    /** @Override */
    override fun enableFab(enabled: Boolean) {
        fabEnabled = enabled
        fabProvider?.invalidateFab()
    }

    /** @Override */
    override val hasNextBowlerOrGame: Boolean
        get() {
            val seriesList = seriesProvider?.seriesList ?: return false
            return seriesList.lastIndex > currentTab || seriesList.any { currentGame < it.numberOfGames - 1 }
        }

    /** @Override */
    override fun nextBowlerOrGame(isEndOfGame: Boolean): GameFragment.NextBowlerResult {
        val seriesList = seriesProvider?.seriesList ?: return GameFragment.NextBowlerResult.None
        if (!hasNextBowlerOrGame) return GameFragment.NextBowlerResult.None

        // Find the next bowler in the remaining list to switch to with games to still play
        var nextSeries = currentTab + 1
        while (nextSeries <= seriesList.lastIndex && seriesList[nextSeries].numberOfGames <= currentGame) {
            nextSeries += 1
        }

        // If there's a bowler found, switch to them and exit
        if (nextSeries <= seriesList.lastIndex) {
            currentTab = nextSeries
            return GameFragment.NextBowlerResult.NextBowler
        }

        // If we were on the last frame, then the next bowler will be on the next game
        val nextGame = if (isEndOfGame) currentGame + 1 else currentGame
        nextSeries = 0

        // Find the first bowler in the list to switch to with games still to play
        while (nextSeries <= seriesList.lastIndex && seriesList[nextSeries].numberOfGames <= nextGame) {
            nextSeries += 1
        }

        // If there's a bowler found, switch to them, update the game, and exit
        var switchedBowler = false
        if (nextSeries <= seriesList.lastIndex) {
            if (currentTab == nextSeries && currentGame == nextGame) {
                // There's no switch happening
                return GameFragment.NextBowlerResult.None
            }
            if (currentTab != nextSeries) {
                currentTab = nextSeries
                switchedBowler = true
            }
            if (currentGame != nextGame) {
                currentGame = nextGame
            }

            return if (switchedBowler) GameFragment.NextBowlerResult.NextBowlerGame else GameFragment.NextBowlerResult.NextGame
        }

        // No next bowler found
        return GameFragment.NextBowlerResult.None
    }

    /**
     * Pager adapter for games.
     */
    class GameControllerPagerAdapter(
        fragmentManager: FragmentManager,
        tabCount: Int,
        private val seriesList: List<Series>?
    ) : BaseFragmentPagerAdapter(fragmentManager, tabCount) {

        /** @Override */
        override fun buildFragment(position: Int): Fragment? {
            seriesList?.let {
                return GameFragment.newInstance(seriesList[position])
            }

            return null
        }
    }
}
