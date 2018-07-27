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

        /** Argument identifier for passing a [Series] to this fragment. */
        private const val ARG_SERIES = "${TAG}_series"

        /**
         * Creates a new instance.
         *
         * @param series the list of series to edit games for
         * @return the new instance
         */
        fun newInstance(series: List<Series>): GameControllerFragment {
            val fragment = GameControllerFragment()
            fragment.arguments = Bundle().apply { putParcelableArray(ARG_SERIES, series.toTypedArray()) }
            return fragment
        }
    }

    /** Controls for the navigation drawer. */
    override lateinit var navigationDrawerController: NavigationDrawerController

    /** The list of series for which games are being edited. */
    private var seriesList: List<Series>? = null

    /** The current series being edited. */
    private val currentSeries: Int
        get() = fragmentPager.currentItem

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
        val parcelableSeries = savedInstanceState?.getParcelableArray(ARG_SERIES) ?: arguments?.getParcelableArray(ARG_SERIES)
        parcelableSeries?.let {
            val mutableSeriesList: MutableList<Series> = ArrayList()
            parcelableSeries.forEach { mutableSeriesList.add(it as Series) }
            seriesList = mutableSeriesList
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun buildPagerAdapter(tabCount: Int): BaseFragmentPagerAdapter {
        return GameControllerPagerAdapter(childFragmentManager, tabCount, seriesList)
    }

    /** @Override */
    override fun addTabs(tabLayout: TabLayout) {
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        seriesList?.let {
            it.forEach {
                tabLayout.addTab(tabLayout.newTab().setText(it.league.bowler.name))
            }
        }
    }

    /** @Override */
    override fun handleTabSwitch(newTab: Int) {
        onSeriesChanged(currentSeries)
    }

    /** @Override */
    override fun onStart() {
        super.onStart()
        val seriesList = seriesList ?: return
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
        onSeriesChanged(currentSeries)
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return if (fabEnabled) R.drawable.ic_arrow_forward else null
    }

    /** @Override */
    override fun onFabClick() {
        if (!fabEnabled) {
            return
        }
        TODO("not implemented")
    }

    /** @Override */
    override fun onNavDrawerItemSelected(@IdRes itemId: Int) {
        val game = NavigationDrawerController.navGameItemIds.indexOf(itemId)
        if (game >= 0) {
            currentGame = game
        }
    }

    /** @Override */
    override fun enableFab(enabled: Boolean) {
        fabEnabled = enabled
        fabProvider?.invalidateFab()
    }

    /**
     * Handle when series changes.
     *
     * @param currentSeries the new series
     */
    private fun onSeriesChanged(currentSeries: Int) {
        seriesList?.let {
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
        val gameFragment = adapter?.getFragment(currentSeries) as? GameFragment
        gameFragment?.gameNumber = currentGame
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
