package ca.josephroque.bowlingcompanion.feature.overview.quickplay

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
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueSummary
import ca.josephroque.bowlingcompanion.core.model.Series
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
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
) : ApproachViewModel<QuickPlayScreenEvent>() {

	private val didDismissQuickPlayTip = userDataRepository.userData.map { it.isQuickPlayTipDismissed }
	private val bowlers = MutableStateFlow(emptyList<Pair<BowlerSummary, LeagueSummary>>())
	private val numberOfGames = MutableStateFlow(Series.DEFAULT_NUMBER_OF_GAMES)

	val uiState: StateFlow<QuickPlayScreenUiState> =
		combine(
			bowlers,
			numberOfGames,
			didDismissQuickPlayTip,
		) { bowlers, numberOfGames, didDismissQuickPlayTip ->
			QuickPlayUiState(
				bowlers = bowlers,
				numberOfGames = numberOfGames,
				isShowingQuickPlayTip = !didDismissQuickPlayTip,
			)
		}
			.map { QuickPlayScreenUiState.Loaded(quickPlay = it) }
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
			is QuickPlayScreenUiAction.EditedLeague -> updateBowlerLeague(action.bowlerId, action.leagueId)
		}
	}

	private fun handleQuickPlayAction(action: QuickPlayUiAction) {
		when (action) {
			QuickPlayUiAction.StartClicked -> startRecording()
			QuickPlayUiAction.AddBowlerClicked -> showBowlerPicker()
			QuickPlayUiAction.BackClicked -> sendEvent(QuickPlayScreenEvent.Dismissed)
			QuickPlayUiAction.TipClicked -> sendEvent(QuickPlayScreenEvent.ShowHowToUseQuickPlay)
			is QuickPlayUiAction.NumberOfGamesChanged -> updateNumberOfGames(action.numberOfGames)
			is QuickPlayUiAction.BowlerClicked -> selectBowlerLeague(action.bowler.id)
			is QuickPlayUiAction.BowlerDeleted -> removeBowler(action.bowler.id)
			is QuickPlayUiAction.BowlerMoved -> moveBowler(action.from, action.to)
		}
	}

	private fun loadDefaultQuickPlay() {
		if (bowlers.value.isNotEmpty()) return
		viewModelScope.launch {
			val defaultBowler = bowlersRepository.getDefaultQuickPlay() ?: return@launch
			bowlers.update { listOf(defaultBowler) }
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
		val bowlers = bowlers.value.map { it.first.id to it.second.id }
		val numberOfGames = numberOfGames.value
		if (bowlers.isEmpty()) return

		val leagueIds = bowlers.map { it.second }
		viewModelScope.launch {
			var firstGameId: UUID? = null
			val seriesIds = leagueIds.map {
				val id = UUID.randomUUID()
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
				bowlers.forEach {
					recentlyUsedRepository.didRecentlyUseBowler(it.first)
					recentlyUsedRepository.didRecentlyUseLeague(it.second)
				}
			}

			val initialGameId = firstGameId ?: return@launch
			sendEvent(QuickPlayScreenEvent.BeganRecording(seriesIds, initialGameId))
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
			val listOffset: Int = if (didDismissQuickPlayTip.first()) 0 else -1

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
