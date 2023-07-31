package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlerQuery
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.bowlerslist.BowlersListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
	bowlersRepository: BowlersRepository
): ViewModel() {
	val bowlersListState: StateFlow<BowlersListUiState> =
		bowlersRepository.getBowlers(BowlerQuery(kind = BowlerKind.PLAYABLE))
			.map(BowlersListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = BowlersListUiState.Loading
			)

	fun deleteBowler(id: UUID) {
		// TODO: prompt delete bowler
	}

	fun navigateToBowler(id: UUID) {
		// TODO: navigate to bowler
	}
}