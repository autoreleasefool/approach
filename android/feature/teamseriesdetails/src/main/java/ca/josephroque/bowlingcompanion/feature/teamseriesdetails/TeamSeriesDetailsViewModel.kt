package ca.josephroque.bowlingcompanion.feature.teamseriesdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamSeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsUiState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TeamSeriesDetailsViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	teamsRepository: TeamsRepository,
	private val teamSeriesRepository: TeamSeriesRepository,
	private val gamesRepository: GamesRepository,
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<TeamSeriesDetailsScreenEvent>() {
	private val teamSeriesId = Route.TeamSeriesDetails.getTeamSeries(savedStateHandle)!!

	private val gameToArchive = MutableStateFlow<TeamSeriesDetailsUiState.GameToArchive?>(null)
	private val gameToRestore = MutableStateFlow<TeamSeriesDetailsUiState.GameToArchive?>(null)

	private val chartModelProducer = ChartEntryModelProducer()

	private val teamDetails = teamsRepository.getTeamSummary(teamSeriesId)
	private val teamSeriesDetails = teamSeriesRepository.getTeamSeriesDetails(teamSeriesId)

	private val topBarUiState =
		teamSeriesDetails.map { TeamSeriesDetailsTopBarUiState(date = it.date) }

	private val teamSeriesDetailsUiState = combine(
		teamDetails,
		teamSeriesDetails,
		gameToArchive,
		gameToRestore,
	) { teamDetails, teamSeriesDetails, gameToArchive, gameToRestore ->
		val isShowingPlaceholder = teamSeriesDetails.scores.all { it == 0 }
		chartModelProducer.setEntriesSuspending(
			if (isShowingPlaceholder) {
				listOf(
					entryOf(0f, 75f),
					entryOf(1f, 200f),
					entryOf(2f, 125f),
					entryOf(3f, 300f),
				)
			} else {
				teamSeriesDetails.scores.mapIndexed { index, score ->
					entryOf(index.toFloat(), score.toFloat())
				}
			},
		).await()

		TeamSeriesDetailsUiState(
			teamSeries = TeamSeriesDetailsUiState.TeamSeries(
				teamName = teamDetails.name,
				date = teamSeriesDetails.date,
				total = teamSeriesDetails.total,
				numberOfGames = teamSeriesDetails.scores.size,
				teamScores = chartModelProducer,
				seriesLow = teamSeriesDetails.scores.minOrNull(),
				seriesHigh = teamSeriesDetails.scores.maxOrNull(),
			),
			listItems = (0..<teamSeriesDetails.scores.size).flatMap { gameIndex ->
				val header = TeamSeriesDetailsUiState.ListItem.GameHeader(
					gameIndex = gameIndex,
					teamTotal = teamSeriesDetails.scores[gameIndex],
				)

				listOf(header) + teamSeriesDetails.members.mapNotNull { member ->
					val game =
						member.games.firstOrNull { game -> game.index == gameIndex } ?: return@mapNotNull null
					TeamSeriesDetailsUiState.ListItem.GameRow(
						gameId = game.id,
						bowlerName = member.name,
						score = game.score,
						isArchived = game.isArchived,
					)
				}
			},
			gameToArchive = gameToArchive,
			gameToRestore = gameToRestore,
			isShowingPlaceholder = isShowingPlaceholder,
		)
	}

	val uiState: StateFlow<TeamSeriesDetailsScreenUiState> = combine(
		topBarUiState,
		teamSeriesDetailsUiState,
	) { topBar, teamSeriesDetails ->
		TeamSeriesDetailsScreenUiState.Loaded(
			topBar = topBar,
			teamSeriesDetails = teamSeriesDetails,
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = TeamSeriesDetailsScreenUiState.Loading,
	)

	fun handleAction(action: TeamSeriesDetailsScreenUiAction) {
		when (action) {
			is TeamSeriesDetailsScreenUiAction.TopBar -> handleTopBarAction(action.action)
			is TeamSeriesDetailsScreenUiAction.TeamSeriesDetails -> handleTeamSeriesAction(action.action)
		}
	}

	private fun handleTopBarAction(action: TeamSeriesDetailsTopBarUiAction) {
		when (action) {
			TeamSeriesDetailsTopBarUiAction.BackClicked -> sendEvent(TeamSeriesDetailsScreenEvent.Dismissed)
			TeamSeriesDetailsTopBarUiAction.AddGameClicked -> addGameToSeries()
		}
	}

	private fun handleTeamSeriesAction(action: TeamSeriesDetailsUiAction) {
		when (action) {
			is TeamSeriesDetailsUiAction.GameClicked -> editGame(action.gameId)
			is TeamSeriesDetailsUiAction.GameArchived -> promptArchiveGame(action.gameId)
			is TeamSeriesDetailsUiAction.GameRestored -> restoreGame(action.gameId)
			TeamSeriesDetailsUiAction.ConfirmArchiveClicked -> confirmArchiveGame()
			TeamSeriesDetailsUiAction.DismissArchiveClicked -> gameToArchive.value = null
			TeamSeriesDetailsUiAction.ConfirmRestoreClicked -> gameToRestore.value = null
		}
	}

	private fun promptArchiveGame(gameId: GameID) {
		viewModelScope.launch {
			gameToArchive.value = getGameToArchive(gameId)
		}
	}

	private fun confirmArchiveGame() {
		val gameToArchive = gameToArchive.value ?: return
		this@TeamSeriesDetailsViewModel.gameToArchive.value = null
		viewModelScope.launch {
			gamesRepository.archiveGame(gameToArchive.gameId)
		}
	}

	private fun restoreGame(gameId: GameID) {
		viewModelScope.launch {
			gameToRestore.value = getGameToArchive(gameId)
			gamesRepository.unarchiveGame(gameId)
		}
	}

	private suspend fun getGameToArchive(gameId: GameID): TeamSeriesDetailsUiState.GameToArchive? {
		val member = teamSeriesDetails.first().members.firstOrNull { member ->
			member.games.any { it.id == gameId }
		} ?: return null
		val game = member.games.firstOrNull { it.id == gameId } ?: return null

		return TeamSeriesDetailsUiState.GameToArchive(
			gameId = gameId,
			bowlerName = member.name,
			gameIndex = game.index,
		)
	}

	private fun editGame(id: GameID) {
		sendEvent(TeamSeriesDetailsScreenEvent.EditGame(teamSeriesId, id))
		analyticsClient.startNewGameSession()
	}

	private fun addGameToSeries() {
		viewModelScope.launch {
			teamSeriesRepository.addGameToTeamSeries(teamSeriesId)
		}
	}
}
