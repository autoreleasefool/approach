package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
	}
}
