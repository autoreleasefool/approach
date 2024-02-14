package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.widget

import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsWidgetCard(
	widget: StatisticsWidget,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Card(modifier = modifier, onClick = onClick) {
	}
}
