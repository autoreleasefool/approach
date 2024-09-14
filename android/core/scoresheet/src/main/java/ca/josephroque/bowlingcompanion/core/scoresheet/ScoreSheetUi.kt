package ca.josephroque.bowlingcompanion.core.scoresheet

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.ScoringGame

data class ScoreSheetUiState(
	val game: ScoringGame? = null,
	val configuration: ScoreSheetConfiguration = ScoreSheetConfiguration(),
	val selection: Selection = Selection(),
) {
	data class Selection(val frameIndex: Int = 0, val rollIndex: Int = 0) {
		companion object {
			fun none() = Selection(frameIndex = -1, rollIndex = -1)
		}
	}
}

data class ScoreSheetListItem(
	val bowler: BowlerSummary,
	val league: LeagueSummary,
	val scoreSheet: ScoreSheetUiState,
)

data class ScoreSheetListUiState(
	val highlightedGame: HighlightedGame? = null,
	val bowlerScores: List<List<ScoreSheetListItem>> = emptyList(),
) {
	data class HighlightedGame(val bowlerId: BowlerID, val gameIndex: Int)
}

sealed interface ScoreSheetUiAction {
	data class RollClicked(val frameIndex: Int, val rollIndex: Int) : ScoreSheetUiAction
	data class FrameClicked(val frameIndex: Int) : ScoreSheetUiAction
}
