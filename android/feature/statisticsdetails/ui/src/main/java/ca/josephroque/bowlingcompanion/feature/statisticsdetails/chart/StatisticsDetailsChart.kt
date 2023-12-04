package ca.josephroque.bowlingcompanion.feature.statisticsdetails.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.core.statistics.charts.CountingChart
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent

@Composable
fun StatisticsDetailsChart(
	state: StatisticsDetailsChartUiState,
	onAction: (StatisticsDetailsChartUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxSize(),
	) {
		if (state.isLoadingNextChart) {
			LoadingState()
		}

		when (state.chartContent?.chart) {
			is StatisticChartContent.CountableChart -> CountingChart(state.chartContent.model)
//			is StatisticChartContent.AveragingChart -> AveragingChart(state.chart.data)
//			is StatisticChartContent.PercentageChart -> PercentageChart(state.chart.data)
//			is StatisticChartContent.DataMissing -> EmptyChart(state.chart.id, tooNarrow = state.isFilterTooNarrow)
//			is StatisticChartContent.ChartUnavailable -> EmptyChart(state.chart.id, tooNarrow = state.isFilterTooNarrow)
			else -> Unit
		}

		Spacer(modifier = Modifier.weight(1f))

		// TODO: Show aggregation picker
	}
}

