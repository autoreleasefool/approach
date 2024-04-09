package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.LabeledSwitch
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.core.model.stub.BowlerSummaryStub
import ca.josephroque.bowlingcompanion.core.statistics.charts.AveragingChart
import ca.josephroque.bowlingcompanion.core.statistics.charts.CountingChart
import ca.josephroque.bowlingcompanion.core.statistics.charts.PercentageChart
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.components.FilterDetails
import ca.josephroque.bowlingcompanion.feature.statisticsdetails.ui.R
import java.util.UUID

@Composable
fun StatisticsDetailsChart(
	state: StatisticsDetailsChartUiState,
	onAction: (StatisticsDetailsChartUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {
		if (state.isShowingTitle && state.chartContent?.chart?.titleResourceId != null) {
			Text(
				text = stringResource(state.chartContent.chart.titleResourceId),
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 8.dp),
			)
		}

		if (state.filterSources != null) {
			FilterDetails(
				filter = state.filter,
				filterSource = state.filterSources,
				modifier = Modifier.padding(bottom = 8.dp),
			)
		}

		if (state.chartContent == null) {
			CircularProgressIndicator(
				modifier = Modifier
					.align(CenterHorizontally)
					.padding(bottom = 8.dp),
			)
		}

		Box(
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 16.dp),
		) {
			when (val chart = state.chartContent?.chart) {
				is StatisticChartContent.CountableChart -> CountingChart(
					chart.data,
					state.chartContent.modelProducer,
				)
				is StatisticChartContent.AveragingChart -> AveragingChart(
					chart.data,
					state.chartContent.modelProducer,
				)
				is StatisticChartContent.PercentageChart -> PercentageChart(
					chart.data,
					state.chartContent.modelProducer,
				)
// 			is StatisticChartContent.PercentageChart -> PercentageChart(state.chart.data)
// 			is StatisticChartContent.DataMissing -> EmptyChart(state.chart.id, tooNarrow = state.isFilterTooNarrow)
// 			is StatisticChartContent.ChartUnavailable -> EmptyChart(state.chart.id, tooNarrow = state.isFilterTooNarrow)
				else -> Unit
			}
		}

		if (state.supportsAggregation) {
			LabeledSwitch(
				checked = state.filter.aggregation == TrackableFilter.AggregationFilter.ACCUMULATE,
				onCheckedChange = { onAction(StatisticsDetailsChartUiAction.AggregationChanged(it)) },
				titleResourceId = R.string.statistics_details_aggregation_title,
				subtitleResourceId = when (state.filter.aggregation) {
					TrackableFilter.AggregationFilter.ACCUMULATE ->
						R.string.statistics_details_aggregation_accumulate_subtitle
					TrackableFilter.AggregationFilter.PERIODIC ->
						R.string.statistics_details_aggregation_periodic_subtitle
				},
				compact = true,
				modifier = Modifier.padding(vertical = 8.dp),
			)
		}
	}
}

@Preview
@Composable
private fun StatisticsDetailsChartPreview() {
	Surface {
		StatisticsDetailsChart(
			state = StatisticsDetailsChartUiState(
				filter = TrackableFilter(source = TrackableFilter.Source.Bowler(UUID.randomUUID())),
				filterSources = TrackableFilter.SourceSummaries(
					bowler = BowlerSummaryStub.single(),
				),
				isFilterTooNarrow = false,
				supportsAggregation = true,
				chartContent = null,
			),
			onAction = {},
			modifier = Modifier.padding(16.dp),
		)
	}
}
