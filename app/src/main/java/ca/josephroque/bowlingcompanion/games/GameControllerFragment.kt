package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.NavigationDrawerController
import ca.josephroque.bowlingcompanion.common.adapters.BaseFragmentPagerAdapter
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.common.interfaces.INavigationDrawerHandler
import ca.josephroque.bowlingcompanion.matchplay.MatchPlayResult
import ca.josephroque.bowlingcompanion.matchplay.MatchPlaySheet
import ca.josephroque.bowlingcompanion.series.Series
import ca.josephroque.bowlingcompanion.settings.Settings
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
        MatchPlaySheet.MatchPlaySheetDelegate,
        IStatisticsContext {

    companion object {
        @Suppress("unused")
        private const val TAG = "GameControllerFragment"

        private const val ARG_SERIES_PROVIDER_TYPE = "${TAG}_type"
        private const val ARG_SERIES_PROVIDER = "${TAG}_series"

        fun newInstance(seriesProvider: SeriesProvider): GameControllerFragment {
            val fragment = GameControllerFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_SERIES_PROVIDER_TYPE, seriesProvider.describeContents())
                putParcelable(ARG_SERIES_PROVIDER, seriesProvider)
            }
            return fragment
        }
    }

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

    override lateinit var navigationDrawerController: NavigationDrawerController
    private var seriesProvider: SeriesProvider? = null
    private var fabEnabled: Boolean = true

    private var currentGame: Int = 0
        set(value) {
            field = value

            currentGameFragment?.gameNumber = currentGame
            navigationDrawerController.gameNumber = currentGame

            Analytics.trackChangedGame()
        }

    private val currentGameFragment: GameFragment?
        get() {
            val adapter = fragmentPager?.adapter as? GameControllerPagerAdapter
            return adapter?.getFragment(currentTab) as? GameFragment
        }

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val seriesType = arguments?.getInt(ARG_SERIES_PROVIDER_TYPE) ?: 0
        seriesProvider = SeriesProvider.getParcelable(arguments, ARG_SERIES_PROVIDER, seriesType)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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

        onSeriesChanged()
        activity?.invalidateOptionsMenu()
        navigationDrawerController.isTeamMember = seriesProvider is SeriesProvider.TeamSeries

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onStop() {
        super.onStop()
        // 0 is the default, as per https://developer.android.com/reference/android/R.attr#windowSoftInputMode
        activity?.window?.setSoftInputMode(0)
    }

    // MARK: TabbedFragment

    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return GameControllerPagerAdapter(childFragmentManager, tabCount, seriesProvider?.seriesList)
    }

    override fun addTabs(tabLayout: TabLayout) {
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        seriesProvider?.seriesList?.let {
            it.forEach { series ->
                tabLayout.addTab(tabLayout.newTab().setText(series.league.bowler.name))
            }
        }
    }

    override fun handleTabSwitch(newTab: Int) {
        onSeriesChanged()
    }

    // MARK: Private functions

    private fun onSeriesChanged() {
        seriesProvider?.seriesList?.let {
            navigationDrawerController.numberOfGames = it[currentTab].numberOfGames
            navigationDrawerController.bowlerName = it[currentTab].league.bowler.name
            navigationDrawerController.leagueName = it[currentTab].league.name
        }

        updateToolbarTitle()
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        seriesProvider?.seriesList?.let { navigationActivity?.setToolbarTitle(it[currentTab].league.bowler.name) }
    }

    // MARK: IFloatingActionButtonHandler

    override fun getFabImage(): Int? {
        val context = context ?: return null
        val userPrefFabEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Settings.EnableFab.prefName, Settings.EnableFab.booleanDefault)
        return if (userPrefFabEnabled && fabEnabled) R.drawable.ic_arrow_forward else null
    }

    override fun onFabClick() {
        if (!fabEnabled) { return }
        currentGameFragment?.onFabClick()
    }

    // MARK: INavigationDrawerHandler

    override fun onNavDrawerItemSelected(@IdRes itemId: Int) {
        val game = NavigationDrawerController.navGameItemIds.indexOf(itemId)
        if (game >= 0) {
            currentGame = game
        }
    }

    // MARK: GameControllerFragment

    fun prepareToPop() {
        currentGameFragment?.prepareToPause()
    }

    // MARK: GameFragmentDelegate

    override val isFabEnabled: Boolean
        get() = fabEnabled

    override fun enableFab(enabled: Boolean) {
        fabEnabled = enabled
        fabProvider?.invalidateFab()
    }

    override val hasNextBowlerOrGame: Boolean
        get() {
            val seriesList = seriesProvider?.seriesList ?: return false
            return seriesList.lastIndex > currentTab || seriesList.any { currentGame < it.numberOfGames - 1 }
        }

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

    override val isFullscreen: Boolean
        get() = navigationActivity?.isFullscreen == true

    override fun toggleFullscreen() {
        navigationActivity?.toggleFullscreen()
    }

    // MARK: MatchPlaySheetDelegate

    override fun onFinishedSettingMatchPlayResults(opponentName: String, opponentScore: Int, matchPlayResult: MatchPlayResult, inputValid: Boolean) {
        currentGameFragment?.onFinishedSettingMatchPlayResults(opponentName, opponentScore, matchPlayResult, inputValid)
    }

    // MARK: GameControllerPagerAdapter

    class GameControllerPagerAdapter(
        fragmentManager: FragmentManager,
        tabCount: Int,
        private val seriesList: List<Series>?
    ) : BaseFragmentPagerAdapter(fragmentManager, tabCount) {

        override fun buildFragment(position: Int): Fragment? {
            seriesList?.let {
                return GameFragment.newInstance(seriesList[position])
            }

            return null
        }
    }
}
