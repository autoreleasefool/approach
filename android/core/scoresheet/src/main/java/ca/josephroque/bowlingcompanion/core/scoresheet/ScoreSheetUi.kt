package ca.josephroque.bowlingcompanion.core.scoresheet

import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.ScoringGame

data class ScoreSheetUiState(
	val game: ScoringGame? = null,
	val configuration: ScoreSheetConfiguration = ScoreSheetConfiguration(),
	val selection: Selection = Selection(),
) {
	data class Selection(
		val frameIndex: Int = 0,
		val rollIndex: Int = 0,
	) {
		companion object {
			fun none() = Selection(frameIndex = -1, rollIndex = -1)
		}
	}
}

data class ScoreSheetListUiState(
	val bowlerScores: List<Triple<BowlerSummary, LeagueSummary, ScoreSheetUiState>> = emptyList(),
)

sealed interface ScoreSheetUiAction {
	data class RollClicked(val frameIndex: Int, val rollIndex: Int) : ScoreSheetUiAction
	data class FrameClicked(val frameIndex: Int) : ScoreSheetUiAction
}
