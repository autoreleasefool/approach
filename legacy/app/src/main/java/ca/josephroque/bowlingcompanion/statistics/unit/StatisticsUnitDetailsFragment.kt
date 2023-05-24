package ca.josephroque.bowlingcompanion.statistics.unit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticHelper
import ca.josephroque.bowlingcompanion.statistics.graph.StatisticGraphFragment
import ca.josephroque.bowlingcompanion.statistics.list.StatisticsListFragment
import ca.josephroque.bowlingcompanion.utils.Analytics

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display details for a [StatisticsUnit].
 */
class StatisticsUnitDetailsFragment : BaseFragment(),
        ListFragment.ListFragmentDelegate,
        StatisticGraphFragment.StatisticGraphDelegate {

    companion object {
        @Suppress("unused")
        private const val TAG = "StatUnitDetailsFragment"

        private const val ARG_UNIT = "${TAG}_unit"

        fun newInstance(unit: StatisticsUnit): StatisticsUnitDetailsFragment {
            return StatisticsUnitDetailsFragment().apply {
                arguments = Bundle().apply { putParcelable(ARG_UNIT, unit) }
            }
        }
    }

    private lateinit var unit: StatisticsUnit

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        unit = arguments?.getParcelable(ARG_UNIT)!!

        val view = inflater.inflate(R.layout.fragment_statistics_unit_details, container, false)

        if (savedInstanceState == null) {
            val fragment = StatisticsListFragment.newInstance(unit)
            childFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, fragment)
                commit()
            }
        }

        return view
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    override fun popChildFragment(): Boolean {
        return childFragmentManager.popBackStackImmediate()
    }

    // MARK: StatisticGraphDelegate

    override fun nextStatistic(statisticId: Long) {
        val (_, nextStatistic) = StatisticHelper.getAdjacentStatistics(statisticId)
        nextStatistic?.let {
            val graphFragment = StatisticGraphFragment.newInstance(unit, nextStatistic.id)
            showStatisticGraph(graphFragment)

            Analytics.trackViewStatisticsGraph(it.getTitle(resources))
        }
    }

    override fun prevStatistic(statisticId: Long) {
        val (prevStatistic, _) = StatisticHelper.getAdjacentStatistics(statisticId)
        prevStatistic?.let {
            val graphFragment = StatisticGraphFragment.newInstance(unit, prevStatistic.id)
            showStatisticGraph(graphFragment)

            Analytics.trackViewStatisticsGraph(it.getTitle(resources))
        }
    }

    // MARK: ListFragmentDelegate

    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (!unit.canShowGraphs) {
            return
        }

        if (item is Statistic) {
            if (item.canBeGraphed) {
                val graphFragment = StatisticGraphFragment.newInstance(unit, item.id)
                showStatisticGraph(graphFragment)

                Analytics.trackViewStatisticsGraph(item.getTitle(resources))
            }
        }
    }

    override fun onItemDeleted(item: IIdentifiable) {
        // Intentionally left blank
    }

    // MARK: Private functions

    private fun showStatisticGraph(graphFragment: StatisticGraphFragment) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, graphFragment)

            if (childFragmentManager.backStackEntryCount == 0) {
                addToBackStack("StatisticsList")
            }

            commit()
        }
    }
}
