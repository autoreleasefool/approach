package ca.josephroque.bowlingcompanion.feature.gameslist.ui

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameListItem

data class GamesListUiState(val list: List<GameListItem>, val gameToArchive: GameListItem?, val isReordering: Boolean)

sealed interface GamesListUiAction {
	data object AddGameClicked : GamesListUiAction

	data class GameClicked(val id: GameID) : GamesListUiAction
	data class GameArchived(val game: GameListItem) : GamesListUiAction
	data class GameMoved(val from: Int, val to: Int, val offset: Int) : GamesListUiAction

	data object ConfirmArchiveClicked : GamesListUiAction
	data object DismissArchiveClicked : GamesListUiAction
}
