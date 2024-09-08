package ca.josephroque.bowlingcompanion.feature.archives

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.TeamSeriesRepository
import ca.josephroque.bowlingcompanion.core.model.ArchivedBowler
import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.ArchivedLeague
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.core.model.ArchivedTeamSeries
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchiveListItem
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchivesListUiAction
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchivesListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ArchivesListViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val leaguesRepository: LeaguesRepository,
	private val seriesRepository: SeriesRepository,
	private val gamesRepository: GamesRepository,
	private val teamSeriesRepository: TeamSeriesRepository,
) : ApproachViewModel<ArchivesListScreenEvent>() {
	private val itemToUnarchive: MutableStateFlow<ArchiveListItem?> = MutableStateFlow(null)

	private val archivesList = combine(
		bowlersRepository.getArchivedBowlers(),
		leaguesRepository.getArchivedLeagues(),
		seriesRepository.getArchivedSeries(),
		gamesRepository.getArchivedGames(),
		teamSeriesRepository.getArchivedTeamSeries(),
	) { bowlers, leagues, series, games, teamSeries ->
		bowlers.map(ArchivedBowler::toArchiveListItem) +
			leagues.map(ArchivedLeague::toArchiveListItem) +
			series.map(ArchivedSeries::toArchiveListItem) +
			games.map(ArchivedGame::toArchiveListItem) +
			teamSeries.map(ArchivedTeamSeries::toArchiveListItem)
				.sortedBy { it.archivedOn }
	}

	val uiState = combine(
		itemToUnarchive,
		archivesList,
	) { itemToUnarchive, archivesList ->
		ArchivesListScreenUiState.Loaded(
			archivesList = ArchivesListUiState(
				list = archivesList,
				itemToUnarchive = itemToUnarchive,
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = ArchivesListScreenUiState.Loading,
	)

	fun handleAction(action: ArchivesListScreenUiAction) {
		when (action) {
			is ArchivesListScreenUiAction.ListAction -> handleListAction(action.action)
		}
	}

	private fun handleListAction(action: ArchivesListUiAction) {
		when (action) {
			is ArchivesListUiAction.BackClicked -> sendEvent(ArchivesListScreenEvent.Dismissed)
			is ArchivesListUiAction.UnarchiveClicked -> itemToUnarchive.value = action.item
			is ArchivesListUiAction.ConfirmUnarchiveClicked -> {
				itemToUnarchive.value?.let { item ->
					viewModelScope.launch {
						when (item) {
							is ArchiveListItem.Bowler -> bowlersRepository.unarchiveBowler(item.bowlerId)
							is ArchiveListItem.League -> leaguesRepository.unarchiveLeague(item.leagueId)
							is ArchiveListItem.Series -> seriesRepository.unarchiveSeries(item.seriesId)
							is ArchiveListItem.Game -> gamesRepository.unarchiveGame(item.gameId)
							is ArchiveListItem.TeamSeries -> teamSeriesRepository.unarchiveTeamSeries(item.teamSeriesId)
						}

						itemToUnarchive.value = null
					}
				}
			}
		}
	}
}

fun ArchivedBowler.toArchiveListItem(): ArchiveListItem.Bowler = ArchiveListItem.Bowler(
	bowlerId = id,
	name = name,
	numberOfLeagues = numberOfLeagues,
	numberOfSeries = numberOfSeries,
	numberOfGames = numberOfGames,
	archivedOn = archivedOn,
)

fun ArchivedLeague.toArchiveListItem(): ArchiveListItem.League = ArchiveListItem.League(
	leagueId = id,
	name = name,
	bowlerName = bowlerName,
	numberOfSeries = numberOfSeries,
	numberOfGames = numberOfGames,
	archivedOn = archivedOn,
)

fun ArchivedSeries.toArchiveListItem(): ArchiveListItem.Series = ArchiveListItem.Series(
	seriesId = id,
	date = date,
	bowlerName = bowlerName,
	leagueName = leagueName,
	numberOfGames = numberOfGames,
	archivedOn = archivedOn,
)

fun ArchivedGame.toArchiveListItem(): ArchiveListItem.Game = ArchiveListItem.Game(
	gameId = id,
	scoringMethod = scoringMethod,
	score = score,
	bowlerName = bowlerName,
	leagueName = leagueName,
	seriesDate = seriesDate,
	archivedOn = archivedOn,
)

fun ArchivedTeamSeries.toArchiveListItem(): ArchiveListItem.TeamSeries = ArchiveListItem.TeamSeries(
	teamSeriesId = id,
	date = date,
	teamName = teamName,
	archivedOn = archivedOn,
)
