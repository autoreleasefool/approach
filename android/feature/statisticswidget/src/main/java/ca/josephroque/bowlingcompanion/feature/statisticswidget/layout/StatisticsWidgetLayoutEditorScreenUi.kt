package ca.josephroque.bowlingcompanion.feature.statisticswidget.layout

import ca.josephroque.bowlingcompanion.feature.statisticswidget.editor.StatisticsWidgetInitialSource
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor.StatisticsWidgetLayoutEditorUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.layout.editor.StatisticsWidgetLayoutEditorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.updateAndGet

sealed interface StatisticsWidgetLayoutEditorScreenUiState {
	data object Loading: StatisticsWidgetLayoutEditorScreenUiState

	data class Loaded(
		val layoutEditor: StatisticsWidgetLayoutEditorUiState
	): StatisticsWidgetLayoutEditorScreenUiState
}

sealed interface StatisticsWidgetLayoutEditorScreenUiAction {
	data object LoadWidgets: StatisticsWidgetLayoutEditorScreenUiAction
	data class LayoutEditor(val action: StatisticsWidgetLayoutEditorUiAction): StatisticsWidgetLayoutEditorScreenUiAction
}

sealed interface StatisticsWidgetLayoutEditorScreenEvent {
	data object Dismissed: StatisticsWidgetLayoutEditorScreenEvent
	data class AddWidget(
		val context: String,
		val initialSource: StatisticsWidgetInitialSource?,
		val priority: Int,
	): StatisticsWidgetLayoutEditorScreenEvent
}

inline fun MutableStateFlow<StatisticsWidgetLayoutEditorScreenUiState>.updateWidgets(
	function: (StatisticsWidgetLayoutEditorUiState) -> StatisticsWidgetLayoutEditorUiState,
): StatisticsWidgetLayoutEditorScreenUiState {
	return this.updateAndGet { state ->
		when (state) {
			StatisticsWidgetLayoutEditorScreenUiState.Loading -> state
			is StatisticsWidgetLayoutEditorScreenUiState.Loaded -> state.copy(layoutEditor = function(state.layoutEditor))
		}
	}
}