package ca.josephroque.bowlingcompanion.feature.statisticswidget.error

import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error.StatisticsWidgetErrorTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error.StatisticsWidgetErrorUiState

data class StatisticsWidgetErrorScreenUiState(
	val statisticsWidgetError: StatisticsWidgetErrorUiState = StatisticsWidgetErrorUiState(),
)

sealed interface StatisticsWidgetErrorScreenUiAction {
	data class TopBar(
		val action: StatisticsWidgetErrorTopBarUiAction,
	) : StatisticsWidgetErrorScreenUiAction
}

sealed interface StatisticsWidgetErrorScreenEvent {
	data object Dismissed : StatisticsWidgetErrorScreenEvent
}
