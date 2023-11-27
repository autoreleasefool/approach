package ca.josephroque.bowlingcompanion.feature.archives

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.ArchivedBowler
import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.ArchivedLeague
import ca.josephroque.bowlingcompanion.core.model.ArchivedSeries
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchiveListItem
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchivesListUiAction
import ca.josephroque.bowlingcompanion.feature.archives.ui.ArchivesListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchivesListViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val leaguesRepository: LeaguesRepository,
	private val seriesRepository: SeriesRepository,
	private val gamesRepository: GamesRepository,
): ApproachViewModel<ArchivesListScreenEvent>() {
	private val _itemToUnarchive: MutableStateFlow<ArchiveListItem?> = MutableStateFlow(null)

	val uiState = combine(
		_itemToUnarchive,
		bowlersRepository.getArchivedBowlers(),
		leaguesRepository.getArchivedLeagues(),
		seriesRepository.getArchivedSeries(),
		gamesRepository.getArchivedGames(),
	) { itemToUnarchive, bowlers, leagues, series, games ->
		ArchivesListScreenUiState.Loaded(
			archivesList = ArchivesListUiState(
				list = (bowlers.map(ArchivedBowler::toArchiveListItem) +
						leagues.map(ArchivedLeague::toArchiveListItem) +
						series.map(ArchivedSeries::toArchiveListItem) +
						games.map(ArchivedGame::toArchiveListItem)).sortedBy { it.archivedOn },
				itemToUnarchive = itemToUnarchive,
			)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = ArchivesListScreenUiState.Loading
	)

	fun handleAction(action: ArchivesListScreenUiAction) {
		when (action) {
			is ArchivesListScreenUiAction.ListAction -> handleListAction(action.action)
		}
	}

	private fun handleListAction(action: ArchivesListUiAction) {
		when (action) {
			is ArchivesListUiAction.BackClicked -> sendEvent(ArchivesListScreenEvent.Dismissed)
			is ArchivesListUiAction.UnarchiveClicked -> _itemToUnarchive.value = action.item
			is ArchivesListUiAction.ConfirmUnarchiveClicked -> {
				_itemToUnarchive.value?.let { item ->
					viewModelScope.launch {
						when (item) {
							is ArchiveListItem.Bowler -> bowlersRepository.unarchiveBowler(item.id)
							is ArchiveListItem.League -> leaguesRepository.unarchiveLeague(item.id)
							is ArchiveListItem.Series -> seriesRepository.unarchiveSeries(item.id)
							is ArchiveListItem.Game -> gamesRepository.unarchiveGame(item.id)
						}

						_itemToUnarchive.value = null
					}
				}
			}
		}
	}
}

fun ArchivedBowler.toArchiveListItem(): ArchiveListItem.Bowler =
	ArchiveListItem.Bowler(
		id = id,
		name = name,
		numberOfLeagues = numberOfLeagues,
		numberOfSeries = numberOfSeries,
		numberOfGames = numberOfGames,
		archivedOn = archivedOn,
	)

fun ArchivedLeague.toArchiveListItem(): ArchiveListItem.League =
	ArchiveListItem.League(
		id = id,
		name = name,
		bowlerName = bowlerName,
		numberOfSeries = numberOfSeries,
		numberOfGames = numberOfGames,
		archivedOn = archivedOn,
	)

fun ArchivedSeries.toArchiveListItem(): ArchiveListItem.Series =
	ArchiveListItem.Series(
		id = id,
		date = date,
		bowlerName = bowlerName,
		leagueName = leagueName,
		numberOfGames = numberOfGames,
		archivedOn = archivedOn,
	)

fun ArchivedGame.toArchiveListItem(): ArchiveListItem.Game =
	ArchiveListItem.Game(
		id = id,
		scoringMethod = scoringMethod,
		score = score,
		bowlerName = bowlerName,
		leagueName = leagueName,
		seriesDate = seriesDate,
		archivedOn = archivedOn,
	)