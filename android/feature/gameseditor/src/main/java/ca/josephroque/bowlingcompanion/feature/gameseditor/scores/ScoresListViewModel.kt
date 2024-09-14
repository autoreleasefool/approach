package ca.josephroque.bowlingcompanion.feature.gameseditor.scores

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.ScoresRepository
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.core.scoresheet.ScorePosition
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetConfiguration
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetListItem
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiAction
import ca.josephroque.bowlingcompanion.core.scoresheet.ScoreSheetUiState
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores.ScoresListUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.scores.ScoresListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ScoresListViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	private val scoresRepository: ScoresRepository,
	private val gamesRepository: GamesRepository,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : ApproachViewModel<ScoresListScreenEvent>() {
	private val series = Route.ScoresList.getSeries(savedStateHandle)
	private val gameIndex = Route.ScoresList.getGameIndex(savedStateHandle) ?: 0
	private val gameIdOrder: MutableStateFlow<Map<GameID, GameOrder>> = MutableStateFlow(emptyMap())

	data class GameOrder(val seriesIndex: Int, val gameIndex: Int)

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
			viewModelScope.launch(ioDispatcher) {
				val games = gamesRepository.getGamesList(seriesId).first()
				val gameDetails = games.map {
					gamesRepository.getGameDetails(it.id).first()
				}
				val scores = games.map {
					scoresRepository.getScore(it.id).first()
				}

				gameIdOrder.update {
					it.toMutableMap().apply {
						games.forEach { game ->
							put(game.id, GameOrder(seriesIndex, game.index))
						}
					}
				}

				val scoreSheetStates = scores.map {
					ScoreSheetUiState(
						game = it,
						selection = ScoreSheetUiState.Selection.none(),
						configuration = ScoreSheetConfiguration(
							scorePosition = setOf(ScorePosition.START, ScorePosition.END),
						),
					)
				}

				_uiState.update {
					it.copy(
						scoreSheetList = it.scoreSheetList.copy(
							it.scoreSheetList.bowlerScores
								.flatten()
								.toMutableList()
								.apply {
									gameDetails.forEach { game ->
										add(
											ScoreSheetListItem(
												game.bowler.toSummary(),
												game.league.toSummary(),
												scoreSheetStates[game.properties.index],
											),
										)
									}
								}
								.groupBy { scoreSheet -> gameIdOrder.value[scoreSheet.scoreSheet.game?.id]?.gameIndex }
								.toList()
								.map { series ->
									series.second
										.sortedBy { scoreSheet -> gameIdOrder.value[scoreSheet.scoreSheet.game?.id]?.seriesIndex }
								},
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
