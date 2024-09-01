package ca.josephroque.bowlingcompanion.feature.gameslist.ui

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameListItem

data class GamesListUiState(val list: List<GameListItem>, val gameToArchive: GameListItem?)

sealed interface GamesListUiAction {
	data object AddGameClicked : GamesListUiAction

	data class GameClicked(val id: GameID) : GamesListUiAction
	data class GameArchived(val game: GameListItem) : GamesListUiAction

	data object ConfirmArchiveClicked : GamesListUiAction
	data object DismissArchiveClicked : GamesListUiAction
}
