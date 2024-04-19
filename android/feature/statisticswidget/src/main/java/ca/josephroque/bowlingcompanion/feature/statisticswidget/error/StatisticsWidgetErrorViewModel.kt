package ca.josephroque.bowlingcompanion.feature.statisticswidget.error

import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.error.StatisticsWidgetErrorTopBarUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class StatisticsWidgetErrorViewModel @Inject constructor() :
	ApproachViewModel<StatisticsWidgetErrorScreenEvent>() {
	private val _uiState: MutableStateFlow<StatisticsWidgetErrorScreenUiState> =
		MutableStateFlow(StatisticsWidgetErrorScreenUiState())
	val uiState = _uiState.asStateFlow()

	fun handleAction(action: StatisticsWidgetErrorScreenUiAction) {
		when (action) {
			is StatisticsWidgetErrorScreenUiAction.TopBar ->
				handleTopBarAction(action.action)
		}
	}

	private fun handleTopBarAction(action: StatisticsWidgetErrorTopBarUiAction) {
		when (action) {
			StatisticsWidgetErrorTopBarUiAction.BackClicked ->
				sendEvent(StatisticsWidgetErrorScreenEvent.Dismissed)
		}
	}
}
