package ca.josephroque.bowlingcompanion.feature.sharing.ui.games

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingAppearance

data class GamesSharingConfigurationUiState(
	val isSeriesDetailChecked: Boolean = true,
	val isSeriesDateChecked: Boolean = true,
	val isBowlerNameChecked: Boolean = false,
	val isLeagueNameChecked: Boolean = false,
	val isGameIncluded: List<IncludedGame> = emptyList(),
	val appearance: SharingAppearance = SharingAppearance.Light,
) {
	data class IncludedGame(
		val gameId: GameID,
		val index: Int,
		val isGameIncluded: Boolean,
	)
}

sealed interface GamesSharingConfigurationUiAction {
	data class IsSeriesDetailCheckedToggled(val isSeriesDetailChecked: Boolean) : GamesSharingConfigurationUiAction
	data class IsSeriesDateCheckedToggled(val isSeriesDateChecked: Boolean) : GamesSharingConfigurationUiAction
	data class IsBowlerNameCheckedToggled(val isBowlerNameChecked: Boolean) : GamesSharingConfigurationUiAction
	data class IsLeagueNameCheckedToggled(val isLeagueNameChecked: Boolean) : GamesSharingConfigurationUiAction
	data class IsGameIncludedToggled(val gameId: GameID, val isGameIncluded: Boolean) : GamesSharingConfigurationUiAction
	data class AppearanceChanged(val appearance: SharingAppearance) : GamesSharingConfigurationUiAction
}
