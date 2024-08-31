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
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.MatchPlayCreate
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.MatchPlayUpdate
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorUiAction
import ca.josephroque.bowlingcompanion.feature.matchplayeditor.ui.MatchPlayEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MatchPlayEditorViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val bowlersRepository: BowlersRepository,
	private val matchPlaysRepository: MatchPlaysRepository,
	private val gamesRepository: GamesRepository,
	private val analyticsClient: AnalyticsClient,
	private val recentlyUsedRepository: RecentlyUsedRepository,
) : ApproachViewModel<MatchPlayEditorScreenEvent>() {
	private val gameId = Route.EditMatchPlay.getGame(savedStateHandle)!!

	private var existingMatchPlay: MatchPlayUpdate? = null
	private var didLoadInitialValue = false
	private val matchPlayEditor: MutableStateFlow<MatchPlayEditorUiState?> = MutableStateFlow(null)

	val uiState: StateFlow<MatchPlayEditorScreenUiState> = matchPlayEditor
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
			initialValue = MatchPlayEditorScreenUiState.Loading,
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
				MatchPlayEditorScreenEvent.EditOpponent(opponent = matchPlayEditor.value?.opponent?.id),
			)
			is MatchPlayEditorUiAction.OpponentScoreChanged -> updateOpponentScore(score = action.score)
			is MatchPlayEditorUiAction.ResultChanged -> updateResult(result = action.result)
		}
	}

	private fun loadMatchPlay() {
		if (didLoadInitialValue) return
		viewModelScope.launch {
			didLoadInitialValue = true
			existingMatchPlay = matchPlaysRepository.getMatchPlay(gameId).first()
			val gameIndex = gamesRepository.getGameIndex(gameId).first()
			matchPlayEditor.value = MatchPlayEditorUiState(
				gameIndex = gameIndex,
				opponent = existingMatchPlay?.opponent,
				opponentScore = existingMatchPlay?.opponentScore,
				result = existingMatchPlay?.result,
			)
		}
	}

	private fun updateOpponent(opponentId: BowlerID?) {
		viewModelScope.launch {
			val opponent = opponentId?.let { bowlersRepository.getBowlerSummary(it).first() }
			matchPlayEditor.update { it?.copy(opponent = opponent) }

			if (opponentId != null) {
				recentlyUsedRepository.didRecentlyUseOpponent(opponentId)
			}
		}
	}

	private fun updateOpponentScore(score: String) {
		matchPlayEditor.update {
			it?.copy(opponentScore = score.toIntOrNull()?.coerceIn(0, Game.MAX_SCORE))
		}
	}
	private fun updateResult(result: MatchPlayResult?) {
		matchPlayEditor.update { it?.copy(result = result) }
	}

	private fun saveMatchPlay() {
		viewModelScope.launch {
			val state = matchPlayEditor.value ?: return@launch
			when (val existingMatchPlay = existingMatchPlay) {
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

					analyticsClient.trackEvent(
						MatchPlayUpdated(
							withOpponent = state.opponent != null,
							withScore = state.opponentScore != null,
							withResult = state.result != null,
						),
					)
				}
			}

			sendEvent(MatchPlayEditorScreenEvent.Dismissed)
		}
	}
}
