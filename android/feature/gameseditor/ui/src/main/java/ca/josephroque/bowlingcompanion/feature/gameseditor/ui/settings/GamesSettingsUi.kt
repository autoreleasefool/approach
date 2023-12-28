package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings

import ca.josephroque.bowlingcompanion.core.model.GameListItem
import java.util.UUID

data class GamesSettingsUiState(
	val currentGameId: UUID,
	val games: List<GameListItem> = emptyList(),
)

sealed interface GamesSettingsUiAction {
	data object	BackClicked: GamesSettingsUiAction

	data class GameClicked(val gameId: UUID): GamesSettingsUiAction
}