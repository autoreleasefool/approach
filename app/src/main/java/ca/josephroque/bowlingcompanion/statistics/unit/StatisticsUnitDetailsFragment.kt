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
import ca.josephroque.bowlingcompanion.statistics.graph.StatisticGraphFragment
import ca.josephroque.bowlingcompanion.statistics.list.StatisticsListFragment

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display details for a [StatisticsUnit].
 */
class StatisticsUnitDetailsFragment : BaseFragment(),
        ListFragment.OnListFragmentInteractionListener,
        StatisticGraphFragment.StatisticGraphDelegate {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatUnitDetailsFragment"

        /** Identifier for the argument that represents the [StatisticsUnit] whose details are displayed. */
        private const val ARG_UNIT = "${TAG}_unit"

        /**
         * Creates a new instance.
         *
         * @param unit unit to load details of
         * @return the new instance
         */
        fun newInstance(unit: StatisticsUnit): StatisticsUnitDetailsFragment {
            return StatisticsUnitDetailsFragment().apply {
                arguments = Bundle().apply { putParcelable(ARG_UNIT, unit) }
            }
        }
    }

    /** The unit whose statistics are to be displayed. */
    private lateinit var unit: StatisticsUnit

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        unit = arguments?.getParcelable(ARG_UNIT)!!

        val view = inflater.inflate(R.layout.fragment_statistics_unit_details, container, false)

        if (savedInstanceState == null) {
            val fragment = StatisticsListFragment.newInstance(unit)
            childFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, fragment)
                commit()
            }
        }

        return view
    }

    /** @Override */
    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: StatisticGraphDelegate

    /** @Override */
    override fun nextStatistic(statisticId: Long) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    /** @Override */
    override fun prevStatistic(statisticId: Long) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    // MARK: OnListFragmentInteractionListener

    /** @Override */
    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is Statistic) {
            if (item.canBeGraphed) {
                val graphFragment = StatisticGraphFragment.newInstance(unit, item.id)
                childFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, graphFragment)
                    commit()
                }
            }
        }
    }
}
