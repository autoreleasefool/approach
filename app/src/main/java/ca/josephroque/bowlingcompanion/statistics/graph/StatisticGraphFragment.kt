package ca.josephroque.bowlingcompanion.statistics.graph

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticHelper
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnit
import kotlinx.android.synthetic.main.fragment_statistic_graph.tv_title as textTitle
import kotlinx.android.synthetic.main.fragment_statistic_graph.tv_prev_statistic as textPrevStatistic
import kotlinx.android.synthetic.main.fragment_statistic_graph.tv_next_statistic as textNextStatistic
import kotlinx.android.synthetic.main.fragment_statistic_graph.chart as chart
import kotlinx.android.synthetic.main.fragment_statistic_graph.tv_accumulate as textAccumulate
import kotlinx.android.synthetic.main.fragment_statistic_graph.switch_accumulate as switchAccumulate
import kotlinx.android.synthetic.main.fragment_statistic_graph.view.*

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Create a graph of a [Statistic] over time.
 */
class StatisticGraphFragment : BaseFragment(),
        View.OnClickListener {

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
        fun newInstance(unit: StatisticsUnit, statisticId: Long): StatisticGraphFragment {
            return StatisticGraphFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_STATISTIC_UNIT, unit)
                    putLong(ARG_STATISTIC, statisticId)
                }
            }
        }
    }

    /** Fragment delegate. */
    private var delegate: StatisticGraphDelegate? = null

    /** Unit to display statistic for. */
    private lateinit var unit: StatisticsUnit

    /** ID of the static to create. */
    private var statisticId: Long = 0

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        unit = arguments?.getParcelable(ARG_STATISTIC_UNIT)!!
        statisticId = arguments?.getLong(ARG_STATISTIC)!!

        val view = inflater.inflate(R.layout.fragment_statistic_graph, container, false)
        view.tv_next_statistic.setOnClickListener(this)
        view.tv_prev_statistic.setOnClickListener(this)
        view.switch_accumulate.setOnCheckedChangeListener { _, _ ->
            updateAccumulateText()
            buildChart()
        }
        return view
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

    /** @Override */
    override fun onStart() {
        super.onStart()

        updateAccumulateText()
        updateStatisticTitles()
        buildChart()
    }

    /** @Override */
    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    /**
     * Set the header, and prev and next button titles to the proper names based on the current statistic.
     */
    private fun updateStatisticTitles() {
        val statistic = StatisticHelper.getStatistic(statisticId)
        val (nextStatistic, previousStatistic) = StatisticHelper.getAdjacentStatistics(statisticId)

        textTitle.setText(statistic.titleId)
        textPrevStatistic.setText(previousStatistic?.titleId ?: 0)
        textNextStatistic.setText(nextStatistic?.titleId ?: 0)
    }

    /**
     * Update the accumulate switch label.
     */
    private fun updateAccumulateText() {
        if (switchAccumulate.isChecked) {
            textAccumulate.setText(R.string.statistic_graph_accumulate)
        } else {
            textAccumulate.setText(R.string.statistic_graph_week_by_week)
        }
    }

    /**
     * Build the chart out of the data from the [StatisticsUnit].
     */
    private fun buildChart() {
    }

    // MARK: OnClickListener

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_prev_statistic -> delegate?.prevStatistic(statisticId)
            R.id.tv_next_statistic -> delegate?.nextStatistic(statisticId)
            else -> {} // Do nothing
        }
    }

    // MARK: StatisticGraphDelegate

    /**
     * Handle events in the fragment.
     */
    interface StatisticGraphDelegate {
        /**
         * Show the next statistic.
         *
         * @param statisticId the current statistic
         */
        fun nextStatistic(statisticId: Long)

        /**
         * Show the previous statistic.
         *
         * @param statisticId the current statistic
         */
        fun prevStatistic(statisticId: Long)
    }
}
