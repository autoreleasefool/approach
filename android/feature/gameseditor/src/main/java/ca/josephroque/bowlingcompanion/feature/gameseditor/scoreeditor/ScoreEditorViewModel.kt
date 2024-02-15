package ca.josephroque.bowlingcompanion.feature.gameseditor.scoreeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scoreeditor.ScoreEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ScoreEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
) : ApproachViewModel<ScoreEditorScreenEvent>() {
	private val initialScore = Route.ScoreEditor.getScore(savedStateHandle) ?: 0
	private val initialScoringMethod = Route.ScoreEditor.getScoringMethod(
		savedStateHandle,
	) ?: GameScoringMethod.BY_FRAME

	private val score = MutableStateFlow(initialScore)
	private val scoringMethod: MutableStateFlow<GameScoringMethod> =
		MutableStateFlow(initialScoringMethod)

	val uiState: StateFlow<ScoreEditorScreenUiState> = combine(
		score,
		scoringMethod,
	) { score, scoringMethod ->
		ScoreEditorScreenUiState.Loaded(
			scoreEditor = ScoreEditorUiState(
				score = score,
				scoringMethod = scoringMethod,
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = ScoreEditorScreenUiState.Loading,
	)

	fun handleAction(action: ScoreEditorScreenUiAction) {
		when (action) {
			is ScoreEditorScreenUiAction.ScoreEditor -> handleScoreEditorAction(action.action)
		}
	}

	private fun handleScoreEditorAction(action: ScoreEditorUiAction) {
		when (action) {
			ScoreEditorUiAction.SaveClicked ->
				sendEvent(ScoreEditorScreenEvent.Dismissed(scoringMethod.value, score.value))
			ScoreEditorUiAction.BackClicked ->
				sendEvent(ScoreEditorScreenEvent.Dismissed(initialScoringMethod, initialScore))
			is ScoreEditorUiAction.ScoreChanged -> updateScore(action.score)
			is ScoreEditorUiAction.ScoringMethodChanged -> updateScoringMethod(action.scoringMethod)
		}
	}

	private fun updateScore(score: String) {
		this.score.value = score.toIntOrNull()?.coerceIn(0..Game.MAX_SCORE) ?: 0
	}

	private fun updateScoringMethod(scoringMethod: GameScoringMethod) {
		this.scoringMethod.value = scoringMethod
	}
}
