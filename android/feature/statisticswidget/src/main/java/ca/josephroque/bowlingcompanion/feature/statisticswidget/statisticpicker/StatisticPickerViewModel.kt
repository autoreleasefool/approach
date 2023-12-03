package ca.josephroque.bowlingcompanion.feature.statisticswidget.statisticpicker

import androidx.lifecycle.SavedStateHandle
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.statistics.widgetStatistics
import ca.josephroque.bowlingcompanion.feature.statisticswidget.navigation.SELECTED_STATISTIC
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.statisticpicker.StatisticPickerUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.statisticpicker.StatisticPickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StatisticPickerViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
): ApproachViewModel<StatisticPickerScreenEvent>() {
	private val _initiallySelectedStatisticTitle = savedStateHandle.get<Int>(SELECTED_STATISTIC) ?: 0

	private val _uiState: MutableStateFlow<StatisticPickerScreenUiState> = widgetStatistics().let {
		MutableStateFlow(StatisticPickerScreenUiState(
			statisticPicker = StatisticPickerUiState(
				statistics = it,
				selectedStatistic = it.firstNotNullOfOrNull { group ->
					group.statistics.firstOrNull { statistic ->
						statistic.titleResourceId == _initiallySelectedStatisticTitle
					}
				} ?: it.first().statistics.first(),
			)
		))
	}

	val uiState = _uiState.asStateFlow()

	fun handleAction(action: StatisticPickerScreenUiAction) {
		when (action) {
			is StatisticPickerScreenUiAction.StatisticPicker -> handleStatisticPickerAction(action.action)
		}
	}

	private fun handleStatisticPickerAction(action: StatisticPickerUiAction) {
		when (action) {
			is StatisticPickerUiAction.BackClicked -> {
				sendEvent(StatisticPickerScreenEvent.Dismissed(_initiallySelectedStatisticTitle))
			}
			is StatisticPickerUiAction.StatisticClicked -> {
				_uiState.value = _uiState.value.copy(
					statisticPicker = _uiState.value.statisticPicker.copy(
						selectedStatistic = action.statistic,
					)
				)
				sendEvent(StatisticPickerScreenEvent.Dismissed(action.statistic.titleResourceId))
			}
		}
	}
}