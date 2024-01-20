package ca.josephroque.bowlingcompanion.feature.matchplayeditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.analytics.trackable.matchplay.MatchPlayCreated
import ca.josephroque.bowlingcompanion.core.analytics.trackable.matchplay.MatchPlayUpdated
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.MatchPlaysRepository
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.MatchPlayCreate
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.MatchPlayUpdate
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorUiAction
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MatchPlayEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
	private val matchPlaysRepository: MatchPlaysRepository,
	private val gamesRepository: GamesRepository,
	private val analyticsClient: AnalyticsClient,
): ApproachViewModel<MatchPlayEditorScreenEvent>() {
	private val gameId = Route.EditMatchPlay.getGame(savedStateHandle)!!

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
		viewModelScope.launch {
			val opponent = opponentId?.let { bowlersRepository.getBowlerSummary(it).first() }
			_matchPlayEditor.update { it?.copy(opponent = opponent) }
		}
	}

	private fun updateOpponentScore(score: String) {
		_matchPlayEditor.update { it?.copy(opponentScore = score.toIntOrNull()?.coerceIn(0, Game.MaxScore)) }
	}

	private fun updateResult(result: MatchPlayResult?) {
		_matchPlayEditor.update { it?.copy(result = result) }
	}

	private fun saveMatchPlay() {
		viewModelScope.launch {
			val state = _matchPlayEditor.value ?: return@launch
			when (val existingMatchPlay = _existingMatchPlay) {
					null -> {
						matchPlaysRepository.insertMatchPlay(
							MatchPlayCreate(
								id = UUID.randomUUID(),
								gameId = gameId,
								opponentId = state.opponent?.id,
								opponentScore = state.opponentScore,
								result = state.result,
							),
						)

						analyticsClient.trackEvent(MatchPlayCreated)
					}
			 else -> {
				 matchPlaysRepository.updateMatchPlay(
					 MatchPlayUpdate(
						 id = existingMatchPlay.id,
						 opponent = state.opponent,
						 opponentScore = state.opponentScore,
						 result = state.result,
					 ),
				 )

				 analyticsClient.trackEvent(MatchPlayUpdated(
					 withOpponent = state.opponent != null,
					 withScore = state.opponentScore != null,
					 withResult = state.result != null,
				 ))
			 }
			}

			sendEvent(MatchPlayEditorScreenEvent.Dismissed)
		}
	}
}