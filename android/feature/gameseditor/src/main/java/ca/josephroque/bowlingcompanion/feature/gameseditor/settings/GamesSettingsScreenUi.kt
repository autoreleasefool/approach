package ca.josephroque.bowlingcompanion.feature.gameseditor.settings

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiState

sealed interface GamesSettingsScreenUiState {
	data object Loading : GamesSettingsScreenUiState

	data class Loaded(val gamesSettings: GamesSettingsUiState) : GamesSettingsScreenUiState
}

sealed interface GamesSettingsScreenUiAction {
	data class GamesSettings(val action: GamesSettingsUiAction) : GamesSettingsScreenUiAction
}

sealed interface GamesSettingsScreenEvent {
	data class DismissedWithResult(val series: List<SeriesID>, val currentGame: GameID) :
		GamesSettingsScreenEvent
}
