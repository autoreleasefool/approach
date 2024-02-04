package ca.josephroque.bowlingcompanion.feature.overview.quickplay

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayUiState
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
class QuickPlayViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val leaguesRepository: LeaguesRepository,
): ApproachViewModel<QuickPlayScreenEvent>() {

	private val _uiState = MutableStateFlow(QuickPlayUiState())

	val uiState: StateFlow<QuickPlayScreenUiState> = _uiState
		.map {
			QuickPlayScreenUiState.Loaded(quickPlay = it)
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
			is QuickPlayScreenUiAction.EditedLeague -> updateBowlerLeague(action.bowlerId, action.leagueId)
		}
	}

	private fun handleQuickPlayAction(action: QuickPlayUiAction) {
		when (action) {
			QuickPlayUiAction.StartClicked -> startRecording()
			QuickPlayUiAction.AddBowlerClicked -> showBowlerPicker()
			QuickPlayUiAction.BackClicked -> sendEvent(QuickPlayScreenEvent.Dismissed)
			is QuickPlayUiAction.NumberOfGamesChanged -> updateNumberOfGames(action.numberOfGames)
			is QuickPlayUiAction.BowlerClicked -> selectBowlerLeague(action.bowler.id)
			is QuickPlayUiAction.BowlerDeleted -> removeBowler(action.bowler.id)
			is QuickPlayUiAction.BowlerMoved -> moveBowler(action.from, action.to)
		}
	}

	private fun loadDefaultQuickPlay() {
		viewModelScope.launch {
			val defaultBowler = bowlersRepository.getDefaultQuickPlay() ?: return@launch
			_uiState.update { it.copy(bowlers = listOf(defaultBowler)) }
		}
	}

	private fun showBowlerPicker() {
		sendEvent(QuickPlayScreenEvent.AddBowler(_uiState.value.bowlers.map { it.first.id }.toSet()))
	}

	private fun updateBowlerLeague(bowlerId: UUID, leagueId: UUID?) {
		if (leagueId == null) {
			removeBowler(bowlerId)
			return
		}

		viewModelScope.launch {
			val bowler = bowlersRepository.getBowlerSummary(bowlerId).first()
			val league = leaguesRepository.getLeagueSummary(leagueId).first()
			_uiState.update {
				it.copy(
					bowlers = it.bowlers.map { bowlerPair ->
						if (bowlerPair.first.id == bowlerId) bowler to league else bowlerPair
					},
				)
			}
		}
	}

	private fun startRecording() {
		val bowlers = _uiState.value.bowlers.map { it.first.id to it.second.id }
		if (bowlers.isEmpty()) return
		sendEvent(QuickPlayScreenEvent.BeganRecording(bowlers))
	}

	private fun selectBowlerLeague(bowlerId: UUID?) {
		bowlerId ?: return
		val leagueId = _uiState.value.bowlers.find { it.first.id == bowlerId }?.second?.id
		sendEvent(QuickPlayScreenEvent.EditLeague(bowlerId = bowlerId, leagueId = leagueId))
	}

	private fun removeBowler(bowlerId: UUID) {
		_uiState.update { it.copy(bowlers = it.bowlers.filter { bowler -> bowler.first.id != bowlerId }) }
	}

	private fun moveBowler(from: Int, to: Int) {
		_uiState.update {
			it.copy(bowlers = it.bowlers.toMutableList().apply { add(to, removeAt(from)) })
		}
	}

	private fun updateNumberOfGames(numberOfGames: Int) {
		_uiState.update { it.copy(numberOfGames = numberOfGames.coerceIn(League.NumberOfGamesRange)) }
	}
}