package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget

import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.designsystem.components.LoadingState
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget

@Composable
fun StatisticsWidgetCard(
	widget: StatisticsWidget,
	chart: StatisticChartContent?,
	modifier: Modifier = Modifier,
	onClick: (() -> Unit)? = null,
) {
	Card(modifier = modifier, onClick = onClick ?: {}) {
		if (chart == null) {
			LoadingState()
		} else {
			Text("Chart")
		}
	}
}
