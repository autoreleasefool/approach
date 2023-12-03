package ca.josephroque.bowlingcompanion.feature.statisticswidget.statisticpicker

import ca.josephroque.bowlingcompanion.core.statistics.StatisticID
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.statisticpicker.StatisticPickerUiAction
import ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.statisticpicker.StatisticPickerUiState

data class StatisticPickerScreenUiState(
	val statisticPicker: StatisticPickerUiState,
)

sealed interface StatisticPickerScreenUiAction {
	data class StatisticPicker(val action: StatisticPickerUiAction): StatisticPickerScreenUiAction
}

sealed interface StatisticPickerScreenEvent {
	data class Dismissed(val result: StatisticID): StatisticPickerScreenEvent
}