package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetID
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetSource
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidgetTimeline
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.placeholder.StatisticsWidgetPlaceholderCard
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget.StatisticsWidgetCard

@Composable
fun StatisticsWidgetLayout(
	state: StatisticsWidgetLayoutUiState,
	onAction: (StatisticsWidgetLayoutUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
		val rowWidth = maxWidth
		val numberOfWidgetsPerRow = (rowWidth / 240.dp).toInt().coerceAtLeast(2)

		Column {
			if (state.widgets.isEmpty()) {
				StatisticsWidgetPlaceholderCard(
					onClick = {
						onAction(StatisticsWidgetLayoutUiAction.ChangeLayoutClicked)
					},
				)
			} else {
				val widgetsRows = state.widgets.chunked(numberOfWidgetsPerRow)
				widgetsRows.forEach { row ->
					StatisticsWidgetRow(
						widgets = row,
						widgetCharts = state.widgetCharts,
						onAction = onAction,
						modifier = Modifier.padding(bottom = if (row == widgetsRows.last()) 0.dp else 16.dp),
						maxWidgetsPerRow = numberOfWidgetsPerRow,
					)
				}

				Row {
					Spacer(modifier = Modifier.weight(1f))

					Surface(
						color = Color.Transparent,
						shape = MaterialTheme.shapes.small,
						onClick = { onAction(StatisticsWidgetLayoutUiAction.ChangeLayoutClicked) },
					) {
						Text(
							stringResource(R.string.statistics_widget_layout_change),
							style = MaterialTheme.typography.labelSmall,
							modifier = Modifier.padding(8.dp),
						)
					}
				}
			}
		}
	}
}

@Composable
private fun StatisticsWidgetRow(
	maxWidgetsPerRow: Int,
	widgets: List<StatisticsWidget>,
	widgetCharts: Map<StatisticsWidgetID, StatisticsWidgetLayoutUiState.ChartContent>,
	onAction: (StatisticsWidgetLayoutUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.fillMaxWidth(),
	) {
		widgets.forEach { widget ->
			val chart = widgetCharts[widget.id]

			StatisticsWidgetCard(
				widget = widget,
				chart = chart?.chart,
				chartEntryModelProducer = chart?.modelProducer,
				onClick = { onAction(StatisticsWidgetLayoutUiAction.WidgetClicked(widget)) },
				modifier = if (maxWidgetsPerRow == 2) {
					Modifier
						.weight(1f)
						.aspectRatio(if (widgets.size == 1) 2f else 1f)
				} else {
					Modifier
						.requiredWidthIn(max = 240.dp)
						.weight(1f, fill = false)
						.aspectRatio(1f)
				},
			)
		}
	}
}

@Preview
@Composable
private fun StatisticsWidgetLayoutPreview() {
	Surface {
		StatisticsWidgetLayout(
			state = StatisticsWidgetLayoutUiState(
				widgetCharts = emptyMap(),
				widgets = listOf(
					StatisticsWidget(
						source = StatisticsWidgetSource.Bowler(BowlerID.randomID()),
						id = StatisticsWidgetID.randomID(),
						timeline = StatisticsWidgetTimeline.THREE_MONTHS,
						statistic = StatisticID.ACES,
						context = "",
						priority = 0,
					),
				),
			),
			onAction = {},
		)
	}
}
