package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.NavigationDrawerController
import ca.josephroque.bowlingcompanion.common.adapters.BaseFragmentPagerAdapter
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.common.interfaces.INavigationDrawerHandler
import ca.josephroque.bowlingcompanion.series.Series
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_pager as fragmentPager
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_tabs as fragmentTabs

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Manage tabs to show games for the team of bowlers.
 */
class GameControllerFragment : TabbedFragment(),
        INavigationDrawerHandler,
        GameFragment.OnGameFragmentInteractionListener {

    companion object {
        /** Logging identifier */
        @Suppress("unused")
        private const val TAG = "GameControllerFragment"

        /** Argument identifier for passing a [SeriesManager] type. */
        private const val ARG_SERIES_MANAGER_TYPE = "${TAG}_type"

        /** Argument identifier for passing a [SeriesManager] to this fragment. */
        private const val ARG_SERIES_MANAGER = "${TAG}_series"

        /**
         * Creates a new instance.
         *
         * @param seriesManager the series to edit games for
         * @return the new instance
         */
        fun newInstance(seriesManager: SeriesManager): GameControllerFragment {
            val fragment = GameControllerFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_SERIES_MANAGER_TYPE, seriesManager.describeContents())
                putParcelable(ARG_SERIES_MANAGER, seriesManager)
            }
            return fragment
        }
    }

    /** Controls for the navigation drawer. */
    override lateinit var navigationDrawerController: NavigationDrawerController

    /** The series being edited. */
    private var seriesManager: SeriesManager? = null

    /** The current game being edited. */
    private var currentGame: Int = 0
        set(value) {
            field = value
            onGameChanged(value)
        }

    /** Indicates if the floating action button should be enabled or not. */
    private var fabEnabled: Boolean = true

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        val seriesType = arguments?.getInt(ARG_SERIES_MANAGER_TYPE) ?: 0
        when (seriesType) {
            0 -> seriesManager = arguments?.getParcelable<SeriesManager.TeamSeries>(ARG_SERIES_MANAGER)
            1 -> seriesManager = arguments?.getParcelable<SeriesManager.BowlerSeries>(ARG_SERIES_MANAGER)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return GameControllerPagerAdapter(childFragmentManager, tabCount, seriesManager?.seriesList)
    }

    /** @Override */
    override fun addTabs(tabLayout: TabLayout) {
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        seriesManager?.seriesList?.let {
            it.forEach { series ->
                tabLayout.addTab(tabLayout.newTab().setText(series.league.bowler.name))
            }
        }
    }

    /** @Override */
    override fun handleTabSwitch(newTab: Int) {
        onSeriesChanged(newTab)
    }

    /** @Override */
    override fun onStart() {
        super.onStart()
        val seriesList = seriesManager?.seriesList ?: return
        if (seriesList.size == 1) {
            fragmentTabs.visibility = View.GONE
            (activity as? AppCompatActivity)?.supportActionBar?.elevation = resources.getDimension(R.dimen.base_elevation)
        } else {
            fragmentTabs.visibility = View.VISIBLE
            (activity as? AppCompatActivity)?.supportActionBar?.elevation = 0F
        }
    }

    /** @Override */
    override fun onResume() {
        super.onResume()
        onSeriesChanged(currentTab)
        activity?.invalidateOptionsMenu()
        navigationDrawerController.isTeamMember = seriesManager is SeriesManager.TeamSeries
    }

    /**
     * Handle when series changes.
     *
     * @param currentSeries the new series
     */
    private fun onSeriesChanged(currentSeries: Int) {
        seriesManager?.seriesList?.let {
            navigationDrawerController.numberOfGames = it[currentSeries].numberOfGames
            navigationDrawerController.bowlerName = it[currentSeries].league.bowler.name
            navigationDrawerController.leagueName = it[currentSeries].league.name
        }
    }

    /**
     * Handle when game changes.
     *
     * @param currentGame the new game
     */
    private fun onGameChanged(currentGame: Int) {
        val adapter = fragmentPager.adapter as? GameControllerPagerAdapter
        val gameFragment = adapter?.getFragment(currentTab) as? GameFragment
        gameFragment?.gameNumber = currentGame
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

    // MARK: OnGameFragmentInteractionListener

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
            val seriesList = seriesManager?.seriesList ?: return false
            return seriesList.size > 1 || currentGame < seriesList[currentTab].numberOfGames - 1
        }

    /** @Override */
    override fun nextBowlerOrGame(isLastFrame: Boolean): Boolean {
        val seriesList = seriesManager?.seriesList ?: return false
        if (!hasNextBowlerOrGame) return false

        // Find the next bowler in the remaining list to switch to with games to still play
        var nextSeries = currentTab + 1
        while (nextSeries <= seriesList.lastIndex && seriesList[nextSeries].numberOfGames <= currentGame) {
            nextSeries += 1
        }

        // If there's a bowler found, switch to them and exit
        if (nextSeries <= seriesList.lastIndex) {
            currentTab = nextSeries
            return true
        }

        // If we were on the last frame, then the next bowler will be on the next game
        val nextGame = if (isLastFrame) currentGame + 1 else currentGame
        nextSeries = 0

        // Find the first bowler in the list to switch to with games still to play
        while (nextSeries <= seriesList.lastIndex && seriesList[nextSeries].numberOfGames <= nextGame) {
            nextSeries += 1
        }

        // If there's a bowler found, switch to them, update the game, and exit
        var switchedBowler = false
        if (nextSeries <= seriesList.lastIndex) {
            if (currentTab != nextSeries) {
                currentTab = nextSeries
                switchedBowler = true
            }
            if (currentGame != nextGame) {
                currentGame = nextGame
            }

            // Only return true if the bowler has switched, not the game
            return switchedBowler
        }

        // No next bowler found
        return false
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
