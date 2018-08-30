package ca.josephroque.bowlingcompanion.statistics.graph

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnit

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Create a graph of a [Statistic] over time.
 */
class StatisticGraphFragment : BaseFragment() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatisticGraphFragment"

        /** Identifier for the argument that represents the [StatisticsUnit] whose details are displayed. */
        private const val ARG_STATISTIC_UNIT = "${TAG}_unit"

        /** Argument identifier for the statistic to be graphed. */
        private const val ARG_STATISTIC = "${TAG}_statistic"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(unit: StatisticsUnit, statisticId: Int): StatisticGraphFragment {
            return StatisticGraphFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_STATISTIC_UNIT, unit)
                    putInt(ARG_STATISTIC, statisticId)
                }
            }
        }
    }

    /** Fragment delegate. */
    private var delegate: StatisticGraphDelegate? = null

    /** @Overrride */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistic_graph, container, false)
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val parent = parentFragment as? StatisticGraphDelegate ?: throw RuntimeException("${parentFragment!!} must implement StatisticGraphDelegate")
        delegate = parent
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    /**
     * Handle events in the fragment.
     */
    interface StatisticGraphDelegate {
        /**
         * Show the next statistic.
         *
         * @param statisticId the current statistic
         */
        fun nextStatistic(statisticId: Int)

        /**
         * Show the previous statistic.
         *
         * @param statisticId the current statistic
         */
        fun prevStatistic(statisticId: Int)
    }
}
