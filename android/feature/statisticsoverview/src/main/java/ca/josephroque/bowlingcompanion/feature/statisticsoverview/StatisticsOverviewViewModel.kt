package ca.josephroque.bowlingcompanion.feature.statisticsoverview

import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.StatisticsOverviewUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StatisticsOverviewViewModel @Inject constructor(
): ApproachViewModel<StatisticsOverviewScreenEvent>() {
	private val _uiState: MutableStateFlow<StatisticsOverviewScreenUiState> =
		MutableStateFlow(StatisticsOverviewScreenUiState.Loaded())
	val uiState = _uiState.asStateFlow()

	fun handleAction(action: StatisticsOverviewScreenUiAction) {
		when (action) {
			is StatisticsOverviewScreenUiAction.StatisticsOverview -> handleOverviewAction(action.action)
		}
	}

	private fun handleOverviewAction(action: StatisticsOverviewUiAction) {
		when (action) {
			StatisticsOverviewUiAction.ViewMoreClicked ->
				sendEvent(StatisticsOverviewScreenEvent.ShowSourcePicker)
		}
	}
}