package ca.josephroque.bowlingcompanion.feature.settings.ui.statistics

data class StatisticsSettingsUiState(
	val isCountingH2AsH: Boolean,
	val isCountingSplitWithBonusAsSplit: Boolean,
	val isHidingZeroStatistics: Boolean,
	val isHidingStatisticDescriptions: Boolean,
	val isHidingWidgetsInBowlersList: Boolean,
	val isHidingWidgetsInLeaguesList: Boolean,
)

sealed interface StatisticsSettingsUiAction {
	data object BackClicked: StatisticsSettingsUiAction

	data class IsCountingH2AsHToggled(val newValue: Boolean): StatisticsSettingsUiAction
	data class IsCountingSplitWithBonusAsSplitToggled(val newValue: Boolean): StatisticsSettingsUiAction
	data class IsHidingZeroStatisticsToggled(val newValue: Boolean): StatisticsSettingsUiAction
	data class IsHidingStatisticDescriptionsToggled(val newValue: Boolean): StatisticsSettingsUiAction
	data class IsHidingWidgetsInBowlersListToggled(val newValue: Boolean): StatisticsSettingsUiAction
	data class IsHidingWidgetsInLeaguesListToggled(val newValue: Boolean): StatisticsSettingsUiAction
}