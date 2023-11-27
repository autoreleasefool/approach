package ca.josephroque.bowlingcompanion.feature.opponentslist

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.feature.opponentslist.ui.OpponentsListUiAction
import ca.josephroque.bowlingcompanion.feature.opponentslist.ui.OpponentsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpponentsListViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
): ApproachViewModel<OpponentsListScreenEvent>() {
	private val _opponentToArchive: MutableStateFlow<BowlerListItem?> = MutableStateFlow(null)

	val uiState = combine(
		bowlersRepository.getOpponentsList(),
		_opponentToArchive,
	) { opponentsList, opponentToArchive ->
		OpponentsListScreenUiState.Loaded(
			list = OpponentsListUiState(
				list = opponentsList,
				opponentToArchive = opponentToArchive,
			)
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = OpponentsListScreenUiState.Loading,
	)

	fun handleAction(action: OpponentsListScreenUiAction) {
		when (action) {
			is OpponentsListScreenUiAction.OpponentsListAction -> handleOpponentsListAction(action.action)
		}
	}

	private fun handleOpponentsListAction(action: OpponentsListUiAction) {
		when (action) {
			is OpponentsListUiAction.BackClicked -> sendEvent(OpponentsListScreenEvent.Dismissed)
			is OpponentsListUiAction.AddOpponentClicked -> sendEvent(OpponentsListScreenEvent.AddOpponent)
			is OpponentsListUiAction.OpponentClicked -> sendEvent(OpponentsListScreenEvent.ShowOpponentDetails(action.id))
			is OpponentsListUiAction.OpponentEdited -> sendEvent(OpponentsListScreenEvent.EditOpponent(action.id))
			is OpponentsListUiAction.OpponentArchived -> setOpponentToArchive(action.opponent)
			is OpponentsListUiAction.DismissArchiveClicked -> setOpponentToArchive(null)
			is OpponentsListUiAction.ConfirmArchiveClicked -> archiveOpponent()
		}
	}

	private fun setOpponentToArchive(opponent: BowlerListItem?) {
		_opponentToArchive.value = opponent
	}

	private fun archiveOpponent() {
		val opponent = _opponentToArchive.value ?: return
		viewModelScope.launch {
			bowlersRepository.archiveBowler(opponent.id)
			setOpponentToArchive(null)
		}
	}
}