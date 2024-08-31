package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import java.util.UUID

data class GamesSettingsUiState(
	val currentBowlerId: BowlerID,
	val bowlers: List<BowlerSummary> = emptyList(),
	val currentGameId: UUID,
	val games: List<GameListItem> = emptyList(),
)

sealed interface GamesSettingsUiAction {
	data object	BackClicked : GamesSettingsUiAction

	data class BowlerClicked(val bowler: BowlerSummary) : GamesSettingsUiAction
	data class BowlerMoved(val from: Int, val to: Int) : GamesSettingsUiAction
	data class GameClicked(val game: GameListItem) : GamesSettingsUiAction
}
