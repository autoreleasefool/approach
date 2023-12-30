package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.R
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.placeholder.StatisticsWidgetPlaceholderCard
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget.StatisticsWidgetCard

@Composable
fun StatisticsWidgetLayout(
	state: StatisticsWidgetLayoutUiState,
	onAction: (StatisticsWidgetLayoutUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth(),
	) {
		if (state.widgets.isEmpty()) {
			StatisticsWidgetPlaceholderCard(onClick = { onAction(StatisticsWidgetLayoutUiAction.ChangeLayoutClicked) })
		} else {
			state.widgets.chunked(2).forEachIndexed { index, row ->
				StatisticsWidgetRow(
					widgets = row,
					onAction = onAction,
					modifier = Modifier.padding(bottom = if (index == state.widgets.size / 2) 0.dp else 16.dp),
				)
			}


			Row {
				Spacer(modifier = Modifier.weight(1f))
				TextButton(onClick = { onAction(StatisticsWidgetLayoutUiAction.ChangeLayoutClicked) }) {
					Text(
						stringResource(R.string.statistics_widget_layout_change),
						style = MaterialTheme.typography.labelSmall,
					)
				}
			}
		}
	}
}

@Composable
private fun StatisticsWidgetRow(
	widgets: List<StatisticsWidget>,
	onAction: (StatisticsWidgetLayoutUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.fillMaxWidth(),
	) {
		widgets.forEach { widget ->
			StatisticsWidgetCard(
				widget = widget,
				onClick = { onAction(StatisticsWidgetLayoutUiAction.WidgetClicked(widget)) },
				modifier = Modifier
					.weight(1f)
					.aspectRatio(if (widgets.size == 1) 2f else 1f),
			)
		}
	}
}