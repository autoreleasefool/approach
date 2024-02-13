package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.LabeledSwitch
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.statistics.charts.CountingChart
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.ui.R

@Composable
fun StatisticsDetailsChart(
	state: StatisticsDetailsChartUiState,
	onAction: (StatisticsDetailsChartUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		if (state.isLoadingNextChart) {
			LoadingState()
		}

		Box(
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 16.dp),
		) {
			when (val chart = state.chartContent?.chart) {
				is StatisticChartContent.CountableChart -> CountingChart(chart.data, state.chartContent.modelProducer)
//			is StatisticChartContent.AveragingChart -> AveragingChart(state.chart.data)
//			is StatisticChartContent.PercentageChart -> PercentageChart(state.chart.data)
//			is StatisticChartContent.DataMissing -> EmptyChart(state.chart.id, tooNarrow = state.isFilterTooNarrow)
//			is StatisticChartContent.ChartUnavailable -> EmptyChart(state.chart.id, tooNarrow = state.isFilterTooNarrow)
				else -> Unit
			}
		}

		if (state.supportsAggregation) {
			LabeledSwitch(
				checked = state.filter.aggregation == TrackableFilter.AggregationFilter.ACCUMULATE,
				onCheckedChange = { onAction(StatisticsDetailsChartUiAction.AggregationChanged(it)) },
				titleResourceId = R.string.statistics_details_aggregation_title,
				subtitleResourceId = when (state.filter.aggregation) {
					TrackableFilter.AggregationFilter.ACCUMULATE -> R.string.statistics_details_aggregation_accumulate_subtitle
					TrackableFilter.AggregationFilter.PERIODIC -> R.string.statistics_details_aggregation_periodic_subtitle
				},
				compact = true,
				modifier = Modifier.padding(vertical = 8.dp)
			)
		}
	}
}

