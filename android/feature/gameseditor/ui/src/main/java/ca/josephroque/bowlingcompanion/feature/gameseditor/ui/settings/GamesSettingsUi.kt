package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSummary

data class GamesSettingsUiState(
	val teamSettings: TeamSettings,
	val bowlerSettings: BowlerSettings,
	val gameSettings: GameSettings,
) {
	data class BowlerSettings(
		val currentBowlerId: BowlerID,
		val bowlers: List<BowlerSummary> = emptyList(),
	)

	data class GameSettings(
		val currentGameId: GameID,
		val games: List<GameListItem> = emptyList(),
	)

	data class TeamSettings(
		val team: TeamSummary?,
		val isShowingTeamScoresInGameDetails: Boolean,
	)
}

sealed interface GamesSettingsUiAction {
	data object	BackClicked : GamesSettingsUiAction

	data class BowlerClicked(val bowler: BowlerSummary) : GamesSettingsUiAction
	data class BowlerMoved(val from: Int, val to: Int) : GamesSettingsUiAction
	data class GameClicked(val game: GameListItem) : GamesSettingsUiAction
	data class ShowTeamScoresInGameDetailsChanged(val isChecked: Boolean) : GamesSettingsUiAction
}
