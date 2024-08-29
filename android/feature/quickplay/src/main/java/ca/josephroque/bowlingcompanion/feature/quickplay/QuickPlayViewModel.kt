package ca.josephroque.bowlingcompanion.feature.quickplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.dispatcher.di.ApplicationScope
import ca.josephroque.bowlingcompanion.core.common.utils.toLocalDate
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.RecentlyUsedRepository
import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.Series
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.navigation.Route
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayTopBarUiState
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayUiAction
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@HiltViewModel
class QuickPlayViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val leaguesRepository: LeaguesRepository,
	private val seriesRepository: SeriesRepository,
	private val gamesRepository: GamesRepository,
	private val recentlyUsedRepository: RecentlyUsedRepository,
	@ApplicationScope private val globalScope: CoroutineScope,
	userDataRepository: UserDataRepository,
	savedStateHandle: SavedStateHandle,
) : ApproachViewModel<QuickPlayScreenEvent>() {
	private val teamId = Route.TeamPlay.getTeam(savedStateHandle)
	private val isTeamQuickPlay = teamId != null

	private val isQuickPlayTipVisible = userDataRepository.userData.map {
		!it.isQuickPlayTipDismissed &&
			!isTeamQuickPlay
	}
	private val bowlers = MutableStateFlow(emptyList<Pair<BowlerSummary, LeagueSummary?>>())
	private val numberOfGames = MutableStateFlow(Series.DEFAULT_NUMBER_OF_GAMES)

	private val topBar = QuickPlayTopBarUiState(
		title = if (isTeamQuickPlay) {
			ca.josephroque.bowlingcompanion.feature.quickplay.ui.R.string.team_play
		} else {
			ca.josephroque.bowlingcompanion.feature.quickplay.ui.R.string.quick_play
		},
		isAddBowlerEnabled = !isTeamQuickPlay,
	)

	val uiState: StateFlow<QuickPlayScreenUiState> =
		combine(
			bowlers,
			numberOfGames,
			isQuickPlayTipVisible,
		) { bowlers, numberOfGames, isQuickPlayTipVisible ->
			QuickPlayUiState(
				bowlers = bowlers,
				numberOfGames = numberOfGames,
				isShowingQuickPlayTip = isQuickPlayTipVisible,
				isStartButtonEnabled = bowlers.isNotEmpty() && bowlers.all { it.second != null },
			)
		}
			.map {
				QuickPlayScreenUiState.Loaded(
					quickPlay = it,
					topBar = topBar,
				)
			}
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = QuickPlayScreenUiState.Loading,
			)

	fun handleAction(action: QuickPlayScreenUiAction) {
		when (action) {
			QuickPlayScreenUiAction.DidAppear -> loadDefaultQuickPlay()
			is QuickPlayScreenUiAction.AddedBowler -> selectBowlerLeague(action.bowlerId)
			is QuickPlayScreenUiAction.QuickPlay -> handleQuickPlayAction(action.action)
			is QuickPlayScreenUiAction.TopBar -> handleTopBarAction(action.action)
			is QuickPlayScreenUiAction.EditedLeague -> updateBowlerLeague(action.bowlerId, action.leagueId)
		}
	}

	private fun handleTopBarAction(action: QuickPlayTopBarUiAction) {
		when (action) {
			QuickPlayTopBarUiAction.AddBowlerClicked -> showBowlerPicker()
			QuickPlayTopBarUiAction.BackClicked -> sendEvent(QuickPlayScreenEvent.Dismissed)
		}
	}

	private fun handleQuickPlayAction(action: QuickPlayUiAction) {
		when (action) {
			QuickPlayUiAction.StartClicked -> startRecording()
			QuickPlayUiAction.TipClicked -> sendEvent(QuickPlayScreenEvent.ShowHowToUseQuickPlay)
			is QuickPlayUiAction.NumberOfGamesChanged -> updateNumberOfGames(action.numberOfGames)
			is QuickPlayUiAction.BowlerClicked -> selectBowlerLeague(action.bowler.id)
			is QuickPlayUiAction.BowlerDeleted -> removeBowler(action.bowler.id)
			is QuickPlayUiAction.BowlerMoved -> moveBowler(action.from, action.to)
		}
	}

	private fun loadDefaultQuickPlay() {
		if (bowlers.value.isNotEmpty()) return

		if (teamId == null) {
			viewModelScope.launch {
				val defaultBowler = bowlersRepository.getDefaultQuickPlay() ?: return@launch
				bowlers.update { listOf(defaultBowler) }
			}
		} else {
			viewModelScope.launch {
				val teamBowlers = bowlersRepository.getTeamBowlers(teamId).first()
				bowlers.update { teamBowlers.map { it to null } }
			}
		}
	}

	private fun showBowlerPicker() {
		sendEvent(QuickPlayScreenEvent.AddBowler(bowlers.value.map { it.first.id }.toSet()))
	}

	private fun updateBowlerLeague(bowlerId: BowlerID, leagueId: LeagueID?) {
		if (leagueId == null) {
			removeBowler(bowlerId)
			return
		}

		viewModelScope.launch {
			val bowler = bowlersRepository.getBowlerSummary(bowlerId).first()
			val league = leaguesRepository.getLeagueSummary(leagueId).first()
			bowlers.update {
				if (it.any { bowlerPair -> bowlerPair.first.id == bowlerId }) {
					it.map { bowlerPair ->
						if (bowlerPair.first.id == bowlerId) bowler to league else bowlerPair
					}
				} else {
					it + (bowler to league)
				}
			}
		}
	}

	private fun startRecording() {
		val teamId = teamId ?: return
		val bowlers = bowlers.value
		val bowlerIds = bowlers.map { it.first.id }
		val leagueIds = bowlers.mapNotNull { it.second?.id }
		val numberOfGames = numberOfGames.value
		if (bowlerIds.isEmpty() || bowlerIds.size != leagueIds.size) return

		if (isTeamQuickPlay) {
			sendEvent(QuickPlayScreenEvent.BeganRecordingTeam(teamId, leagueIds))
			return
		}

		viewModelScope.launch {
			var firstGameId: GameID? = null
			val seriesIds = leagueIds.map {
				val id = SeriesID.randomID()
				seriesRepository.insertSeries(
					SeriesCreate(
						leagueId = it,
						id = id,
						date = Clock.System.now().toLocalDate(),
						appliedDate = null,
						numberOfGames = numberOfGames,
						preBowl = SeriesPreBowl.REGULAR,
						excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
						alleyId = null,
						manualScores = null,
					),
				)

				if (firstGameId == null) {
					val games = gamesRepository.getGameIds(seriesId = id).first()
					firstGameId = games.first()
				}

				return@map id
			}

			globalScope.launch {
				bowlerIds.zip(leagueIds).forEach {
					recentlyUsedRepository.didRecentlyUseBowler(it.first)
					recentlyUsedRepository.didRecentlyUseLeague(it.second)
				}
			}

			val initialGameId = firstGameId ?: return@launch
			sendEvent(QuickPlayScreenEvent.BeganRecordingSeries(seriesIds, initialGameId))
		}
	}

	private fun selectBowlerLeague(bowlerId: BowlerID?) {
		bowlerId ?: return
		val leagueId = bowlers.value.find { it.first.id == bowlerId }?.second?.id
		sendEvent(QuickPlayScreenEvent.EditLeague(bowlerId = bowlerId, leagueId = leagueId))
	}

	private fun removeBowler(bowlerId: BowlerID) {
		bowlers.update { it.filter { bowler -> bowler.first.id != bowlerId } }
	}

	private fun moveBowler(fromListIndex: Int, toListIndex: Int) {
		viewModelScope.launch {
			// Depends on number of `item` before bowlers in `QuickPlay#LazyColumn`
			val listOffset: Int = if (isQuickPlayTipVisible.first()) -1 else 0

			val from = fromListIndex + listOffset
			val to = toListIndex + listOffset
			bowlers.update {
				if (from == to || !it.indices.contains(from) || !it.indices.contains(to)) return@update it
				it.toMutableList().apply { add(to, removeAt(from)) }
			}
		}
	}

	private fun updateNumberOfGames(numberOfGames: Int) {
		this.numberOfGames.update { numberOfGames.coerceIn(League.NumberOfGamesRange) }
	}
}
