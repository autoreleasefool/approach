package ca.josephroque.bowlingcompanion.feature.gameseditor.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSummary
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.gameseditor.ui.settings.GamesSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GamesSettingsViewModel @Inject constructor(
	bowlersRepository: BowlersRepository,
	teamsRepository: TeamsRepository,
	private val gamesRepository: GamesRepository,
	private val userDataRepository: UserDataRepository,
	savedStateHandle: SavedStateHandle,
) : ApproachViewModel<GamesSettingsScreenEvent>() {
	private val teamSeriesId = Route.GameSettings.getTeamSeries(savedStateHandle)
	private val initialSeries = Route.GameSettings.getSeries(savedStateHandle)
	private val initialGameId = Route.GameSettings.getCurrentGame(savedStateHandle)!!

	private val team: MutableStateFlow<TeamSummary?> = MutableStateFlow(null)

	private val currentGameId = MutableStateFlow(initialGameId)
	private val games: MutableStateFlow<List<GameListItem>> = MutableStateFlow(emptyList())

	private val currentBowlerId = MutableStateFlow(BowlerID.randomID())
	private val bowlers: MutableStateFlow<List<Pair<SeriesID, BowlerSummary>>> =
		MutableStateFlow(emptyList())

	private val isShowingTeamScoresInGameDetails = userDataRepository.userData.map {
		!it.isHidingTeamScoresInGameDetails
	}

	init {
		viewModelScope.launch {
			val bowlers = initialSeries
				.zip(bowlersRepository.getSeriesBowlers(initialSeries).first())
			this@GamesSettingsViewModel.bowlers.update { bowlers }

			val currentGameDetails = gamesRepository.getGameDetails(initialGameId).first()
			val currentSeriesId = currentGameDetails.series.id
			val seriesGames = gamesRepository.getGamesList(currentSeriesId).first()
			games.update { seriesGames }
			currentBowlerId.update { currentGameDetails.bowler.id }

			teamSeriesId?.let {
				val teamSummary = teamsRepository.getTeamSummary(it).first()
				team.value = teamSummary
			}
		}
	}

	private val teamSettings = combine(
		team,
		isShowingTeamScoresInGameDetails,
	) { team, isShowingTeamScoresInGameDetails ->
		GamesSettingsUiState.TeamSettings(
			team = team,
			isShowingTeamScoresInGameDetails = isShowingTeamScoresInGameDetails,
		)
	}

	private val bowlerSettings = combine(
		currentBowlerId,
		bowlers,
	) { currentBowlerId, bowlers ->
		GamesSettingsUiState.BowlerSettings(
			currentBowlerId = currentBowlerId,
			bowlers = bowlers.map { it.second },
		)
	}

	private val gameSettings = combine(
		currentGameId,
		games,
	) { currentGameId, games ->
		GamesSettingsUiState.GameSettings(
			currentGameId = currentGameId,
			games = games,
		)
	}

	val uiState: StateFlow<GamesSettingsScreenUiState> = combine(
		teamSettings,
		bowlerSettings,
		gameSettings,
	) { team, bowlers, games ->
		GamesSettingsScreenUiState.Loaded(
			GamesSettingsUiState(
				teamSettings = team,
				bowlerSettings = bowlers,
				gameSettings = games,
			),
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
			is GamesSettingsUiAction.ShowTeamScoresInGameDetailsChanged ->
				setIsShowingTeamScores(action.isChecked)
		}
	}

	private fun dismiss() {
		sendEvent(
			GamesSettingsScreenEvent.DismissedWithResult(
				bowlers.value.map { it.first },
				currentGameId.value,
			),
		)
	}

	private fun setCurrentGame(gameId: GameID) {
		currentGameId.value = gameId
		dismiss()
	}

	private fun setCurrentBowler(bowlerId: BowlerID) {
		viewModelScope.launch {
			val currentGameIndex = games.value.indexOfFirst { it.id == currentGameId.value }
			val currentSeriesId = bowlers.value.first { it.second.id == bowlerId }.first
			val seriesGames = gamesRepository.getGamesList(currentSeriesId).first()
			games.update { seriesGames }
			currentGameId.update { seriesGames[currentGameIndex].id }
			currentBowlerId.update { bowlerId }
			dismiss()
		}
	}

	private fun moveBowler(fromListIndex: Int, toListIndex: Int) {
		// Depends on number of `item` before bowlers in `GamesSettings#LazyColumn`
		val from = fromListIndex - 2
		val to = toListIndex - 2
		bowlers.update {
			if (from == to || !it.indices.contains(from) || !it.indices.contains(to)) return@update it
			it.toMutableList().apply { add(to, removeAt(from)) }
		}
	}

	private fun setIsShowingTeamScores(isChecked: Boolean) {
		viewModelScope.launch {
			userDataRepository.setIsHidingTeamScoresInGameDetails(!isChecked)
		}
	}
}
