package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget

data class StatisticsWidgetLayoutUiState(
	val widgets: List<StatisticsWidget>,
)

sealed interface StatisticsWidgetLayoutUiAction {
	data object ChangeLayoutClicked : StatisticsWidgetLayoutUiAction
	data class WidgetClicked(val widget: StatisticsWidget) : StatisticsWidgetLayoutUiAction
}
