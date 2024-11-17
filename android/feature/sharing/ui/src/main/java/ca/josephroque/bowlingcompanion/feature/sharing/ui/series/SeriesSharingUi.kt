package ca.josephroque.bowlingcompanion.feature.sharing.ui.series

data class SeriesSharingUiState(
	val isDateChecked: Boolean = true,
	val isSummaryChecked: Boolean = true,
	val isBowlerChecked: Boolean = false,
	val isLeagueChecked: Boolean = false,
	val isHighScoreChecked: Boolean = false,
	val isLowScoreChecked: Boolean = false,
	val chartRange: IntRange = IntRange.EMPTY,
	val chartLowerBoundRange: IntRange = IntRange.EMPTY,
	val chartUpperBoundRange: IntRange = IntRange.EMPTY,
	val appearance: SharingAppearance,
)

enum class SharingAppearance {
	Light,
	Dark,
}

sealed interface SeriesSharingUiAction {
	data class IsDateCheckedToggled(val isDateChecked: Boolean) : SeriesSharingUiAction
	data class IsSummaryCheckedToggled(val isSummaryChecked: Boolean) : SeriesSharingUiAction
	data class IsBowlerCheckedToggled(val isBowlerChecked: Boolean) : SeriesSharingUiAction
	data class IsLeagueCheckedToggled(val isLeagueChecked: Boolean) : SeriesSharingUiAction
	data class IsHighScoreCheckedToggled(val isHighScoreChecked: Boolean) : SeriesSharingUiAction
	data class IsLowScoreCheckedToggled(val isLowScoreChecked: Boolean) : SeriesSharingUiAction
	data class ChartRangeMinimumChanged(val minimum: Int) : SeriesSharingUiAction
	data class ChartRangeMaximumChanged(val maximum: Int) : SeriesSharingUiAction
	data class AppearanceChanged(val appearance: SharingAppearance) : SeriesSharingUiAction
}
