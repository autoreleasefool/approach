package ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult

data class MatchPlayEditorUiState(
	val gameIndex: Int,
	val opponent: BowlerSummary?,
	val opponentScore: Int? = null,
	val result: MatchPlayResult? = null,
)

sealed interface MatchPlayEditorUiAction {
	data object BackClicked: MatchPlayEditorUiAction
	data object DoneClicked: MatchPlayEditorUiAction
	data object OpponentClicked: MatchPlayEditorUiAction

	data class OpponentScoreChanged(val score: String): MatchPlayEditorUiAction
	data class ResultChanged(val result: MatchPlayResult?): MatchPlayEditorUiAction
}