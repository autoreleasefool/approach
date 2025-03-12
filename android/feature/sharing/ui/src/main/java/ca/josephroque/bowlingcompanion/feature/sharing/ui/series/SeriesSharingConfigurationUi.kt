package ca.josephroque.bowlingcompanion.feature.sharing.ui.series

import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance

data class SeriesSharingConfigurationUiState(
	val isDateChecked: Boolean = true,
	val isBowlerChecked: Boolean = false,
	val isLeagueChecked: Boolean = false,
	val isSeriesTotalChecked: Boolean = true,
	val isHighScoreChecked: Boolean = false,
	val isLowScoreChecked: Boolean = false,
	val chartRange: IntRange = IntRange.EMPTY,
	val chartLowerBoundRange: IntRange = IntRange.EMPTY,
	val chartUpperBoundRange: IntRange = IntRange.EMPTY,
	val appearance: SharingAppearance = SharingAppearance.Light,
) {
	val isAnySummaryItemShowing: Boolean
		get() = isSeriesTotalChecked || isLowScoreChecked || isHighScoreChecked

	fun performAction(action: SeriesSharingConfigurationUiAction): SeriesSharingConfigurationUiState {
		return when (action) {
			is SeriesSharingConfigurationUiAction.IsDateCheckedToggled -> copy(isDateChecked = action.isDateChecked)
			is SeriesSharingConfigurationUiAction.IsBowlerCheckedToggled -> copy(isBowlerChecked = action.isBowlerChecked)
			is SeriesSharingConfigurationUiAction.IsLeagueCheckedToggled -> copy(isLeagueChecked = action.isLeagueChecked)
			is SeriesSharingConfigurationUiAction.IsSeriesTotalCheckedToggled -> copy(isSeriesTotalChecked = action.isSeriesTotalChecked)
			is SeriesSharingConfigurationUiAction.IsHighScoreCheckedToggled -> copy(isHighScoreChecked = action.isHighScoreChecked)
			is SeriesSharingConfigurationUiAction.IsLowScoreCheckedToggled -> copy(isLowScoreChecked = action.isLowScoreChecked)
			is SeriesSharingConfigurationUiAction.ChartRangeMinimumChanged -> copy(chartRange = IntRange(action.minimum, chartRange.last))
			is SeriesSharingConfigurationUiAction.ChartRangeMaximumChanged -> copy(chartRange = IntRange(chartRange.first, action.maximum))
			is SeriesSharingConfigurationUiAction.AppearanceChanged -> copy(appearance = action.appearance)
		}
	}
}

sealed interface SeriesSharingConfigurationUiAction {
	data class IsDateCheckedToggled(val isDateChecked: Boolean) : SeriesSharingConfigurationUiAction
	data class IsBowlerCheckedToggled(val isBowlerChecked: Boolean) : SeriesSharingConfigurationUiAction
	data class IsLeagueCheckedToggled(val isLeagueChecked: Boolean) : SeriesSharingConfigurationUiAction
	data class IsSeriesTotalCheckedToggled(val isSeriesTotalChecked: Boolean) : SeriesSharingConfigurationUiAction
	data class IsHighScoreCheckedToggled(val isHighScoreChecked: Boolean) : SeriesSharingConfigurationUiAction
	data class IsLowScoreCheckedToggled(val isLowScoreChecked: Boolean) : SeriesSharingConfigurationUiAction
	data class ChartRangeMinimumChanged(val minimum: Int) : SeriesSharingConfigurationUiAction
	data class ChartRangeMaximumChanged(val maximum: Int) : SeriesSharingConfigurationUiAction
	data class AppearanceChanged(val appearance: SharingAppearance) : SeriesSharingConfigurationUiAction
}
