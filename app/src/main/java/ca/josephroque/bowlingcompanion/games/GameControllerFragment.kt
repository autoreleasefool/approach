package ca.josephroque.bowlingcompanion.games

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.TabbedFragment
import ca.josephroque.bowlingcompanion.series.Series

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Manage tabs to show games for the team of bowlers.
 */
class GameControllerFragment : TabbedFragment() {

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
            val args = Bundle()
            args.putParcelableArray(ARG_SERIES, series.toTypedArray())
            fragment.arguments = args
            return fragment
        }
    }

    /** The list of series for which games are being edited. */
    private var seriesList: List<Series>? = null

    /** The current series being edited. */
    private var currentSeries: Int = 0

    /** The current game being edited. */
    private var currentGame: Int = 0

    /** @Override */
    @Suppress("UNCHECKED_CAST")
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
        seriesList?.let {
            it.forEach {
                tabLayout.addTab(tabLayout.newTab().setText(it.league.bowler.name))
            }
        }
    }

    /** @Override */
    override fun getFabImage(): Int? {
        return R.drawable.ic_arrow_forward
    }

    /** @Override */
    override fun onFabClick() {

        TODO("not implemented")
    }

    /**
     * Pager adapter for games.
     */
    class GameControllerPagerAdapter(
            fragmentManager: FragmentManager,
            tabCount: Int,
            private val seriesList: List<Series>?
    ): BaseFragmentPagerAdapter(fragmentManager, tabCount) {

        /** @Override */
        override fun buildFragment(position: Int): Fragment? {
            seriesList?.let {
                return GameFragment.newInstance(seriesList[position])
            }

            return null
        }
    }

}