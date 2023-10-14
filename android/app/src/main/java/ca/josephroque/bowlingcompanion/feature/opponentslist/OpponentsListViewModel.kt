package ca.josephroque.bowlingcompanion.feature.opponentslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OpponentsListViewModel @Inject constructor(
	bowlersRepository: BowlersRepository,
): ViewModel() {
	val opponentsListState: StateFlow<OpponentsListUiState> =
		bowlersRepository.getOpponentsList()
			.map(OpponentsListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = OpponentsListUiState.Loading,
			)
}

sealed interface OpponentsListUiState {
	data object Loading: OpponentsListUiState
	data class Success(
		val list: List<OpponentListItem>
	): OpponentsListUiState
}