package ca.josephroque.bowlingcompanion.feature.matchplayeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.MatchPlaysRepository
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.MatchPlayCreate
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.MatchPlayUpdate
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.navigation.GAME_ID
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorUiAction
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MatchPlayEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
	private val matchPlaysRepository: MatchPlaysRepository,
	private val gamesRepository: GamesRepository,
): ApproachViewModel<MatchPlayEditorScreenEvent>() {
	private val gameId = savedStateHandle.get<String>(GAME_ID)!!.let { UUID.fromString(it) }

	private var _existingMatchPlay: MatchPlayUpdate? = null
	private var didLoadInitialValue = false
	private val _matchPlayEditor: MutableStateFlow<MatchPlayEditorUiState?> = MutableStateFlow(null)

	val uiState: StateFlow<MatchPlayEditorScreenUiState> = _matchPlayEditor
		.map { matchPlayEditor ->
			if (matchPlayEditor == null) {
				MatchPlayEditorScreenUiState.Loading
			} else {
				MatchPlayEditorScreenUiState.Loaded(matchPlayEditor)
			}
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = MatchPlayEditorScreenUiState.Loading
		)

	fun handleAction(action: MatchPlayEditorScreenUiAction) {
		when (action) {
			MatchPlayEditorScreenUiAction.LoadMatchPlay -> loadMatchPlay()
			is MatchPlayEditorScreenUiAction.UpdatedOpponent -> updateOpponent(action.opponent)
			is MatchPlayEditorScreenUiAction.MatchPlayEditor -> handleMatchPlayEditorAction(action.action)
		}
	}

	private fun handleMatchPlayEditorAction(action: MatchPlayEditorUiAction) {
		when (action) {
			MatchPlayEditorUiAction.BackClicked -> sendEvent(MatchPlayEditorScreenEvent.Dismissed)
			MatchPlayEditorUiAction.DoneClicked -> saveMatchPlay()
			MatchPlayEditorUiAction.OpponentClicked -> sendEvent(
				MatchPlayEditorScreenEvent.EditOpponent(opponent = _matchPlayEditor.value?.opponent?.id)
			)
			is MatchPlayEditorUiAction.OpponentScoreChanged -> updateOpponentScore(score = action.score)
			is MatchPlayEditorUiAction.ResultChanged -> updateResult(result = action.result)
		}
	}

	private fun loadMatchPlay() {
		if (didLoadInitialValue) return
		viewModelScope.launch {
			didLoadInitialValue = true
			_existingMatchPlay = matchPlaysRepository.getMatchPlay(gameId).first()
			val gameIndex = gamesRepository.getGameIndex(gameId).first()
			_matchPlayEditor.value = MatchPlayEditorUiState(
				gameIndex = gameIndex,
				opponent = _existingMatchPlay?.opponent,
				opponentScore = _existingMatchPlay?.opponentScore,
				result = _existingMatchPlay?.result,
			)
		}
	}

	private fun updateOpponent(opponentId: UUID?) {
		// TODO: prevent other form updates while opponent is loading
		viewModelScope.launch {
			val state = _matchPlayEditor.value ?: return@launch
			val opponent = opponentId?.let { bowlersRepository.getBowlerSummary(it).first() }
			_matchPlayEditor.value = state.copy(opponent = opponent)
		}
	}

	private fun updateOpponentScore(score: String) {
		val state = _matchPlayEditor.value ?: return
		_matchPlayEditor.value = state.copy(opponentScore = score.toIntOrNull()?.coerceIn(0, Game.MaxScore))
	}

	private fun updateResult(result: MatchPlayResult?) {
		val state = _matchPlayEditor.value ?: return
		_matchPlayEditor.value = state.copy(result = result)
	}

	private fun saveMatchPlay() {
		viewModelScope.launch {
			val state = _matchPlayEditor.value ?: return@launch
			when (val existingMatchPlay = _existingMatchPlay) {
				null -> matchPlaysRepository.insertMatchPlay(MatchPlayCreate(
					id = UUID.randomUUID(),
					gameId = gameId,
					opponentId = state.opponent?.id,
					opponentScore = state.opponentScore,
					result = state.result,
				))
			 else ->
				matchPlaysRepository.updateMatchPlay(MatchPlayUpdate(
					id = existingMatchPlay.id,
					opponent = state.opponent,
					opponentScore = state.opponentScore,
					result = state.result,
				))
			}

			sendEvent(MatchPlayEditorScreenEvent.Dismissed)
		}
	}
}