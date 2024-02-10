package ca.josephroque.bowlingcompanion.feature.gameseditor

import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores.ScoresListUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores.ScoresListUiState

sealed interface ScoresListScreenUiState {
	data object Loading: ScoresListScreenUiState
	data class Loaded(
		val scoresList: ScoresListUiState,
	): ScoresListScreenUiState
}

sealed interface ScoresListScreenUiAction {
	data class ScoresList(val action: ScoresListUiAction): ScoresListScreenUiAction
}

sealed interface ScoresListScreenEvent {
	data object Dismissed: ScoresListScreenEvent
}