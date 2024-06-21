package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget.StatisticsWidgetCard

@Composable
fun StatisticsWidgetError(state: StatisticsWidgetErrorUiState, modifier: Modifier = Modifier) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier
			.verticalScroll(rememberScrollState())
			.padding(horizontal = 16.dp),
	) {
		Card {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.padding(16.dp),
			) {
				StatisticsWidgetCard(
					widget = state.widget,
					chart = StatisticChartContent.DataMissing(StatisticID.GAME_AVERAGE),
					chartEntryModelProducer = state.widgetChart,
					modifier = Modifier
						.widthIn(max = 200.dp)
						.padding(bottom = 8.dp),
				)

				Text(
					text = stringResource(R.string.statistics_widget_not_enough_data_error_title),
					style = MaterialTheme.typography.titleMedium,
				)

				Text(
					text = stringResource(R.string.statistics_widget_not_enough_data_error_description_1),
					style = MaterialTheme.typography.bodyMedium,
				)

				Text(
					text = stringResource(R.string.statistics_widget_not_enough_data_error_description_2),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}

		Card {
			Column(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.padding(16.dp),
			) {
				StatisticsWidgetCard(
					widget = state.widget,
					chart = StatisticChartContent.ChartUnavailable(StatisticID.GAME_AVERAGE),
					chartEntryModelProducer = state.widgetChart,
					modifier = Modifier
						.widthIn(max = 200.dp)
						.padding(bottom = 8.dp),
				)

				Text(
					text = stringResource(R.string.statistics_widget_unavailable_error_title),
					style = MaterialTheme.typography.titleMedium,
				)

				Text(
					text = stringResource(R.string.statistics_widget_unavailable_error_description_1),
					style = MaterialTheme.typography.bodyMedium,
				)

				Text(
					text = stringResource(R.string.statistics_widget_unavailable_error_description_2),
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}
	}
}

@Preview
@Composable
private fun StatisticsWidgetEditorPreview() {
	MaterialTheme {
		StatisticsWidgetError(
			state = StatisticsWidgetErrorUiState(),
		)
	}
}
