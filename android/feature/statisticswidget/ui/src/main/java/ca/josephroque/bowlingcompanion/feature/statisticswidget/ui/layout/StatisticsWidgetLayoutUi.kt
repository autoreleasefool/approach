package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticChartContent
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget
import java.util.UUID

data class StatisticsWidgetLayoutUiState(
	val widgets: List<StatisticsWidget>,
	val widgetCharts: Map<UUID, StatisticChartContent>,
)

sealed interface StatisticsWidgetLayoutUiAction {
	data object ChangeLayoutClicked : StatisticsWidgetLayoutUiAction
	data class WidgetClicked(val widget: StatisticsWidget) : StatisticsWidgetLayoutUiAction
}
