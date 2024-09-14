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
import ca.josephroque.bowlingcompanion.core.data.repository.TeamSeriesRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.Series
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesConnect
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.navigation.ResourcePickerResultKey
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

val QUICK_PLAY_BOWLER_RESULT_KEY = ResourcePickerResultKey("QuickPlayBowlerResultKey")
val QUICK_PLAY_LEAGUE_RESULT_KEY = ResourcePickerResultKey("QuickPlayLeagueResultKey")

@HiltViewModel
class QuickPlayViewModel @Inject constructor(
	private val teamSeriesRepository: TeamSeriesRepository,
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
	private val leagueRecurrence = MutableStateFlow(LeagueRecurrence.REPEATING)
	private val leagueName = MutableStateFlow("")
	private var selectingLeagueForBowler: BowlerID? = null

	private val topBar = QuickPlayTopBarUiState(
		title = if (isTeamQuickPlay) {
			ca.josephroque.bowlingcompanion.feature.quickplay.ui.R.string.team_play
		} else {
			ca.josephroque.bowlingcompanion.feature.quickplay.ui.R.string.quick_play
		},
		isAddBowlerEnabled = !isTeamQuickPlay,
		formStyle = if (isTeamQuickPlay) {
			QuickPlayTopBarUiState.FormStyle.Normal
		} else {
			QuickPlayTopBarUiState.FormStyle.Sheet
		},
	)

	val uiState: StateFlow<QuickPlayScreenUiState> =
		combine(
			bowlers,
			numberOfGames,
			isQuickPlayTipVisible,
			leagueRecurrence,
			leagueName,
		) { bowlers, numberOfGames, isQuickPlayTipVisible, leagueRecurrence, leagueName ->
			QuickPlayUiState(
				bowlers = when (leagueRecurrence) {
					LeagueRecurrence.REPEATING -> bowlers
					LeagueRecurrence.ONCE -> {
						if (leagueName.isBlank()) {
							bowlers.map { it.first to null }
						} else {
							val league = LeagueSummary(
								id = LeagueID.randomID(),
								name = leagueName,
							)
							bowlers.map { it.first to league }
						}
					}
				},
				numberOfGames = numberOfGames,
				isShowingQuickPlayTip = isQuickPlayTipVisible,
				isStartButtonEnabled = if (isTeamQuickPlay) {
					leagueRecurrence == LeagueRecurrence.REPEATING || leagueName.isNotBlank()
				} else {
					bowlers.isNotEmpty() && bowlers.all { it.second != null }
				},
				leagueRecurrence = leagueRecurrence,
				leagueName = leagueName,
				leagueNameErrorId = if (leagueName.isBlank()) {
					ca.josephroque.bowlingcompanion.feature.quickplay.ui.R.string
						.team_play_league_recurrence_league_name_required_error
				} else {
					null
				},
				isShowingLeagueRecurrencePicker = isTeamQuickPlay,
				isDeleteBowlersEnabled = !isTeamQuickPlay,
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
			is QuickPlayScreenUiAction.EditedLeague -> updateBowlerLeague(action.leagueId)
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
			is QuickPlayUiAction.LeagueNameChanged -> updateLeagueName(action.name)
			is QuickPlayUiAction.LeagueRecurrenceChanged -> updateLeagueRecurrence(action.recurrence)
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

	private fun updateBowlerLeague(leagueId: LeagueID?) {
		val bowlerId = selectingLeagueForBowler ?: return
		selectingLeagueForBowler = null

		if (leagueId == null) {
			if (isTeamQuickPlay) {
				bowlers.update {
					it.map { bowlerPair ->
						if (bowlerPair.first.id == bowlerId) bowlerPair.first to null else bowlerPair
					}
				}
			} else {
				removeBowler(bowlerId)
			}
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
		val bowlers = bowlers.value
		val bowlerIds = bowlers.map { it.first.id }
		val leagueIds = bowlers.mapNotNull { it.second?.id }

		viewModelScope.launch {
			if (isTeamQuickPlay) {
				startRecordingTeamSeries(
					teamId = teamId ?: return@launch,
					bowlerIds = bowlerIds,
					leagueIds = leagueIds,
					recurrence = leagueRecurrence.value,
					leagueName = leagueName.value,
					numberOfGames = numberOfGames.value,
				)
			} else {
				startRecordingSeries(
					bowlerIds = bowlerIds,
					leagueIds = leagueIds,
					numberOfGames = numberOfGames.value,
				)
			}
		}
	}

	private suspend fun startRecordingTeamSeries(
		teamId: TeamID,
		bowlerIds: List<BowlerID>,
		leagueIds: List<LeagueID>,
		recurrence: LeagueRecurrence,
		leagueName: String,
		numberOfGames: Int,
	) {
		when (recurrence) {
			LeagueRecurrence.REPEATING -> {
				if (bowlerIds.isEmpty() || bowlerIds.size != leagueIds.size) return
				sendEvent(QuickPlayScreenEvent.TeamLeaguesSelected(teamId, leagueIds))
			}
			LeagueRecurrence.ONCE -> {
				if (bowlerIds.isEmpty() || leagueName.isBlank()) return

				val events = bowlerIds.map {
					LeagueCreate(
						id = LeagueID.randomID(),
						bowlerId = it,
						name = leagueName,
						recurrence = LeagueRecurrence.ONCE,
						numberOfGames = numberOfGames,
						additionalPinFall = null,
						additionalGames = null,
						excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
					)
				}

				leaguesRepository.insertAllLeagues(events)
				val eventSeries = seriesRepository.getEventSeriesIdsList(events.map { it.id }).first()
				val teamSeriesId = TeamSeriesID.randomID()
				teamSeriesRepository.insertTeamSeries(
					TeamSeriesConnect(
						id = teamSeriesId,
						teamId = teamId,
						seriesIds = eventSeries,
						date = Clock.System.now().toLocalDate(),
					),
				)
				val initialGameId = gamesRepository.getGamesList(eventSeries.first()).first().first().id
				sendEvent(QuickPlayScreenEvent.TeamEventsCreated(teamSeriesId, initialGameId))
			}
		}
	}

	private suspend fun startRecordingSeries(
		bowlerIds: List<BowlerID>,
		leagueIds: List<LeagueID>,
		numberOfGames: Int,
	) {
		if (bowlerIds.isEmpty() || bowlerIds.size != leagueIds.size) return

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

		val initialGameId = firstGameId ?: return
		sendEvent(QuickPlayScreenEvent.BeganRecordingSeries(seriesIds, initialGameId))
	}

	private fun selectBowlerLeague(bowlerId: BowlerID?) {
		bowlerId ?: return
		val leagueId = bowlers.value.find { it.first.id == bowlerId }?.second?.id
		selectingLeagueForBowler = bowlerId
		sendEvent(QuickPlayScreenEvent.EditLeague(bowlerId = bowlerId, leagueId = leagueId))
	}

	private fun removeBowler(bowlerId: BowlerID) {
		bowlers.update { it.filter { bowler -> bowler.first.id != bowlerId } }
	}

	private fun moveBowler(fromListIndex: Int, toListIndex: Int) {
		viewModelScope.launch {
			// Depends on number of `item` before bowlers in `QuickPlay#LazyColumn`
			val from = fromListIndex - 1
			val to = toListIndex - 1
			bowlers.update {
				if (from == to || !it.indices.contains(from) || !it.indices.contains(to)) return@update it
				it.toMutableList().apply { add(to, removeAt(from)) }
			}
		}
	}

	private fun updateNumberOfGames(numberOfGames: Int) {
		this.numberOfGames.update { numberOfGames.coerceIn(League.NumberOfGamesRange) }
	}

	private fun updateLeagueName(name: String) {
		leagueName.update { name }
	}

	private fun updateLeagueRecurrence(recurrence: LeagueRecurrence) {
		leagueRecurrence.update { recurrence }
	}
}
