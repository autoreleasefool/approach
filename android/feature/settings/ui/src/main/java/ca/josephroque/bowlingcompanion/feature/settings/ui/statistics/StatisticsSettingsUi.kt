package ca.josephroque.bowlingcompanion.feature.settings.ui.statistics

data class StatisticsSettingsUiState(
	val isCountingH2AsH: Boolean,
	val isCountingSplitWithBonusAsSplit: Boolean,
	val isHidingZeroStatistics: Boolean,
	val isHidingWidgetsInBowlersList: Boolean,
	val isHidingWidgetsInLeaguesList: Boolean,
)

sealed interface StatisticsSettingsUiAction {
	data object BackClicked: StatisticsSettingsUiAction

	data class ToggleIsCountingH2AsH(val newValue: Boolean?): StatisticsSettingsUiAction
	data class ToggleIsCountingSplitWithBonusAsSplit(val newValue: Boolean?): StatisticsSettingsUiAction
	data class ToggleIsHidingZeroStatistics(val newValue: Boolean?): StatisticsSettingsUiAction
	data class ToggleIsHidingWidgetsInBowlersList(val newValue: Boolean?): StatisticsSettingsUiAction
	data class ToggleIsHidingWidgetsInLeaguesList(val newValue: Boolean?): StatisticsSettingsUiAction
}