package ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores

import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetListUiState
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiAction

data class ScoresListUiState(
	val gameIndex: Int,
	val scoreSheetList: ScoreSheetListUiState = ScoreSheetListUiState(),
)

sealed interface ScoresListUiAction {
	data object BackClicked : ScoresListUiAction

	data class ScoreSheet(val action: ScoreSheetUiAction): ScoresListUiAction
}