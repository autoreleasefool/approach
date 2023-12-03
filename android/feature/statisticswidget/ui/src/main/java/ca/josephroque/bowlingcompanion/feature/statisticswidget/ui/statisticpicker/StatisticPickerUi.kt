package ca.josephroque.bowlingcompanion.feature.statisticswidget.ui.statisticpicker

import ca.josephroque.bowlingcompanion.core.statistics.Statistic
import ca.josephroque.bowlingcompanion.core.statistics.models.StatisticGroup

data class StatisticPickerUiState(
	val statistics: List<StatisticGroup>,
	val selectedStatistic: Statistic,
)

sealed interface StatisticPickerUiAction {
	data object BackClicked: StatisticPickerUiAction
	data class StatisticClicked(val statistic: Statistic): StatisticPickerUiAction
}