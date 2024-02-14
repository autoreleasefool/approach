package ca.josephroque.bowlingcompanion.feature.gameseditor

import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiState
import java.util.UUID

sealed interface GamesSettingsScreenUiState {
	data object Loading : GamesSettingsScreenUiState

	data class Loaded(
		val gamesSettings: GamesSettingsUiState,
	) : GamesSettingsScreenUiState
}

sealed interface GamesSettingsScreenUiAction {
	data class GamesSettings(val action: GamesSettingsUiAction) : GamesSettingsScreenUiAction
}

sealed interface GamesSettingsScreenEvent {
	data class DismissedWithResult(
		val series: List<UUID>,
		val currentGame: UUID,
	) : GamesSettingsScreenEvent
}
