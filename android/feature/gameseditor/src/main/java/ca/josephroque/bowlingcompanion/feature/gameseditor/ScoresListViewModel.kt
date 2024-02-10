package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.ScoresRepository
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.scoresheet.ScorePosition
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetConfiguration
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiAction
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores.ScoresListUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores.ScoresListUiState
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
class ScoresListViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val scoresRepository: ScoresRepository,
	private val gamesRepository: GamesRepository,
): ApproachViewModel<ScoresListScreenEvent>() {
	private val series = Route.ScoresList.getSeries(savedStateHandle)
	private val gameIndex = Route.ScoresList.getGameIndex(savedStateHandle) ?: 0
	private val _gameIdOrder: MutableStateFlow<Map<UUID, Int>> = MutableStateFlow(emptyMap())

	private val _uiState = MutableStateFlow(ScoresListUiState(gameIndex = gameIndex))
	val uiState: StateFlow<ScoresListScreenUiState> = _uiState
		.map { ScoresListScreenUiState.Loaded(it) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = ScoresListScreenUiState.Loading,
		)

	init {
		series.forEachIndexed { seriesIndex, seriesId ->
			viewModelScope.launch {
				val games = gamesRepository.getGamesList(seriesId).first()
				val currentGameId = games[gameIndex].id
				val currentGame = gamesRepository.getGameDetails(currentGameId).first()

				_gameIdOrder.update {
					it.toMutableMap().apply {
						put(currentGameId, seriesIndex)
					}
				}

				val score = scoresRepository.getScore(currentGameId).first()
				val scoreSheetState = ScoreSheetUiState(
					game = score,
					selection = ScoreSheetUiState.Selection(frameIndex = -1, rollIndex = -1),
					configuration = ScoreSheetConfiguration(
						scorePosition = setOf(ScorePosition.START, ScorePosition.END),
					)
				)

				_uiState.update {
					println("${_gameIdOrder.value}")
					it.copy(
						scoreSheetList = it.scoreSheetList.copy(
							it.scoreSheetList.bowlerScores.toMutableList().apply {
								add(Triple(
									currentGame.bowler.toSummary(),
									currentGame.league.toSummary(),
									scoreSheetState,
								))

								sortBy { _gameIdOrder.value[currentGameId] ?: Int.MAX_VALUE }
							}
						),
					)
				}
			}
		}
	}

	fun handleAction(action: ScoresListScreenUiAction) {
		when (action) {
			is ScoresListScreenUiAction.ScoresList -> handleScoresListAction(action.action)
		}
	}

	private fun handleScoresListAction(action: ScoresListUiAction) {
		when (action) {
			is ScoresListUiAction.BackClicked -> sendEvent(ScoresListScreenEvent.Dismissed)
			is ScoresListUiAction.ScoreSheet -> handleScoreSheetAction(action.action)
		}
	}

	private fun handleScoreSheetAction(action: ScoreSheetUiAction) {
		when (action) {
			is ScoreSheetUiAction.FrameClicked -> Unit
			is ScoreSheetUiAction.RollClicked -> Unit
		}
	}
}