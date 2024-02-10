package ca.josephroque.bowlingcompanion.feature.gameseditor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GamesSettingsViewModel @Inject constructor(
	bowlersRepository: BowlersRepository,
	private val gamesRepository: GamesRepository,
	savedStateHandle: SavedStateHandle,
): ApproachViewModel<GamesSettingsScreenEvent>() {
	private val initialSeries = Route.GameSettings.getSeries(savedStateHandle)
	private val initialGameId = Route.GameSettings.getCurrentGame(savedStateHandle)!!

	private val _currentGameId = MutableStateFlow(initialGameId)
	private val _games: MutableStateFlow<List<GameListItem>> = MutableStateFlow(emptyList())

	private val _currentBowlerId = MutableStateFlow(UUID.randomUUID())
	private val _bowlers: MutableStateFlow<List<Pair<UUID, BowlerSummary>>> = MutableStateFlow(emptyList())

	init {
		viewModelScope.launch {
			val bowlers = initialSeries
				.zip(bowlersRepository.getSeriesBowlers(initialSeries).first())
			_bowlers.update { bowlers }

			val currentGameDetails = gamesRepository.getGameDetails(initialGameId).first()
			val currentSeriesId = currentGameDetails.series.id
			val seriesGames = gamesRepository.getGamesList(currentSeriesId).first()
			_games.update { seriesGames }
			_currentBowlerId.update { currentGameDetails.bowler.id }
		}
	}

	val uiState: StateFlow<GamesSettingsScreenUiState> = combine(
		_currentGameId,
		_games,
		_currentBowlerId,
		_bowlers,
	) { currentGame, games, currentBowler, bowlers ->
		GamesSettingsScreenUiState.Loaded(
			GamesSettingsUiState(
				currentBowlerId = currentBowler,
				bowlers = bowlers.map { it.second },
				currentGameId = currentGame,
				games = games,
			)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = GamesSettingsScreenUiState.Loading,
	)

	fun handleAction(action: GamesSettingsScreenUiAction) {
		when (action) {
			is GamesSettingsScreenUiAction.GamesSettings -> handleGamesSettingsAction(action.action)
		}
	}

	private fun handleGamesSettingsAction(action: GamesSettingsUiAction) {
		when (action) {
			GamesSettingsUiAction.BackClicked -> dismiss()
			is GamesSettingsUiAction.BowlerClicked -> setCurrentBowler(action.bowler.id)
			is GamesSettingsUiAction.BowlerMoved -> moveBowler(action.from, action.to)
			is GamesSettingsUiAction.GameClicked -> setCurrentGame(action.game.id)
		}
	}

	private fun dismiss() {
		sendEvent(GamesSettingsScreenEvent.DismissedWithResult(
			_bowlers.value.map { it.first },
			_currentGameId.value,
		))
	}

	private fun setCurrentGame(gameId: UUID) {
		_currentGameId.value = gameId
	}

	private fun setCurrentBowler(bowlerId: UUID) {
		viewModelScope.launch {
			val currentGameIndex = _games.value.indexOfFirst { it.id == _currentGameId.value }
			val currentSeriesId = _bowlers.value.first { it.second.id == bowlerId }.first
			val seriesGames = gamesRepository.getGamesList(currentSeriesId).first()
			_games.update { seriesGames }
			_currentGameId.update { seriesGames[currentGameIndex].id }
			_currentBowlerId.update { bowlerId }
		}
	}

	private fun moveBowler(fromListIndex: Int, toListIndex: Int) {
		// Depends on number of `item` before bowlers in `GamesSettings#LazyColumn`
		val from = fromListIndex - 1
		val to = toListIndex - 1
		_bowlers.update {
			if (from == to || !it.indices.contains(from) || !it.indices.contains(to)) return@update it
			it.toMutableList().apply { add(to, removeAt(from)) }
		}
	}
}