package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor

import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticsWidget

data class StatisticsWidgetLayoutEditorUiState(
	val widgets: List<StatisticsWidget> = emptyList(),
	val isDeleteModeEnabled: Boolean = false,
)

data class StatisticsWidgetLayoutEditorTopBarUiState(
	val isDeleteModeEnabled: Boolean = false,
)

sealed interface StatisticsWidgetLayoutEditorUiAction {
	data object BackClicked : StatisticsWidgetLayoutEditorUiAction
	data object AddWidgetClicked : StatisticsWidgetLayoutEditorUiAction

	data class WidgetClicked(val widget: StatisticsWidget) : StatisticsWidgetLayoutEditorUiAction
	data class WidgetMoved(val from: Int, val to: Int) : StatisticsWidgetLayoutEditorUiAction
	data class ToggleDeleteMode(val deleteMode: Boolean) : StatisticsWidgetLayoutEditorUiAction
}
