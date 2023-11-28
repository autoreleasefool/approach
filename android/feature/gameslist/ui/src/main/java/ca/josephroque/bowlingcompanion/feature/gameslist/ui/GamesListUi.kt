package ca.josephroque.bowlingcompanion.feature.gameslist.ui

import ca.josephroque.bowlingcompanion.core.model.GameListItem
import java.util.UUID

data class GamesListUiState(
	val list: List<GameListItem>,
	val gameToArchive: GameListItem?,
)

sealed interface GamesListUiAction {
	data object AddGameClicked: GamesListUiAction

	data class GameClicked(val id: UUID): GamesListUiAction
	data class GameArchived(val game: GameListItem): GamesListUiAction

	data object ConfirmArchiveClicked: GamesListUiAction
	data object DismissArchiveClicked: GamesListUiAction
}