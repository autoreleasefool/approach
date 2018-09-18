package ca.josephroque.bowlingcompanion.statistics.graph

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.settings.Settings
import ca.josephroque.bowlingcompanion.statistics.Statistic
import ca.josephroque.bowlingcompanion.statistics.StatisticHelper
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnit
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
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
        @Suppress("unused")
        private const val TAG = "StatisticGraphFragment"

        private const val ARG_STATISTIC_UNIT = "${TAG}_unit"
        private const val ARG_STATISTIC = "${TAG}_statistic"

        fun newInstance(unit: StatisticsUnit, statisticId: Long): StatisticGraphFragment {
            return StatisticGraphFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_STATISTIC_UNIT, unit)
                    putLong(ARG_STATISTIC, statisticId)
                }
            }
        }
    }

    private var delegate: StatisticGraphDelegate? = null
    private lateinit var unit: StatisticsUnit
    private var statisticId: Long = 0

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        unit = arguments?.getParcelable(ARG_STATISTIC_UNIT)!!
        statisticId = arguments?.getLong(ARG_STATISTIC)!!

        val view = inflater.inflate(R.layout.fragment_statistic_graph, container, false)
        view.tv_next_statistic.setOnClickListener(this)
        view.tv_prev_statistic.setOnClickListener(this)
        view.switch_accumulate.setOnCheckedChangeListener { _, isChecked ->
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putBoolean(Settings.AccumulateStatistics.prefName, isChecked)
                    .apply()
            updateAccumulateText()
            buildChart()
        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val parent = parentFragment as? StatisticGraphDelegate ?: throw RuntimeException("${parentFragment!!} must implement StatisticGraphDelegate")
        delegate = parent
    }

    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    override fun onStart() {
        super.onStart()

        updateAccumulateText()
        updateStatisticTitles()
        buildChart()
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: Private functions

    private fun updateStatisticTitles() {
        val statistic = StatisticHelper.getStatistic(statisticId)
        val (previousStatistic, nextStatistic) = StatisticHelper.getAdjacentStatistics(statisticId)

        textTitle.setText(statistic.titleId)
        if (previousStatistic != null) {
            textPrevStatistic.setText(previousStatistic.titleId)
        } else {
            textPrevStatistic.text = null
        }

        if (nextStatistic != null) {
            textNextStatistic.setText(nextStatistic.titleId)
        } else {
            textNextStatistic.text = null
        }
    }

    private fun updateAccumulateText() {
        val accumulateOverTime = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Settings.AccumulateStatistics.prefName, Settings.AccumulateStatistics.booleanDefault)
        switchAccumulate.isChecked = accumulateOverTime

        if (accumulateOverTime) {
            textAccumulate.setText(R.string.statistic_graph_accumulate)
        } else {
            textAccumulate.setText(R.string.statistic_graph_week_by_week)
        }
    }

    private fun addChartDataStyling(context: Context, dataSet: LineDataSet, lineIndex: Int) {
        val color = when (lineIndex) {
            0 -> ContextCompat.getColor(context, R.color.colorPrimary)
            1 -> ContextCompat.getColor(context, R.color.dangerRed)
            else -> throw IllegalAccessException("Only up to 2 lines are available for a statistic.")
        }

        dataSet.color = color
        dataSet.setCircleColor(color)
    }

    private fun buildChartData(context: Context, graphLines: List<StatisticsGraphLine>): Deferred<LineData> {
        return async(CommonPool) {
            val dataSets: List<LineDataSet> = graphLines.mapIndexed { index, graphLine ->
                val dataSet = LineDataSet(graphLine.entries, graphLine.label)
                addChartDataStyling(context, dataSet, index)
                return@mapIndexed dataSet
            }

            return@async LineData(dataSets.toList())
        }
    }

    private fun buildChartXAxisFormatter(graphLabels: List<String>): IAxisValueFormatter {
        return IAxisValueFormatter { value, _ -> graphLabels[value.toInt()] }
    }

    private fun buildChart() {
        val context = context ?: return
        launch(Android) {
            val (graphLines, graphLabels) = unit.getStatisticGraphData(context, statisticId, switchAccumulate.isChecked).await()
            chart.data = buildChartData(context, graphLines).await()
            chart.xAxis.valueFormatter = buildChartXAxisFormatter(graphLabels)
            chart.xAxis.labelCount = graphLabels.size
            fixChartProperties()
            chart.invalidate()
        }
    }

    private fun fixChartProperties() {
        chart.description = Description().apply { text = "" }
        chart.legend.apply {
            isEnabled = true
            textSize = resources.getDimension(R.dimen.text_caption_dp) / resources.displayMetrics.density
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            form = Legend.LegendForm.LINE
        }
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

    interface StatisticGraphDelegate {
        fun nextStatistic(statisticId: Long)
        fun prevStatistic(statisticId: Long)
    }
}
