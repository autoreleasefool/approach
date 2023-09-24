package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.feature.bowlerslist.BowlersListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
	bowlersRepository: BowlersRepository
): ViewModel() {
	val bowlersListState: StateFlow<BowlersListUiState> =
		bowlersRepository.getBowlersList()
			.map(BowlersListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = BowlersListUiState.Loading
			)

	fun deleteBowler(id: UUID) {
		// TODO: prompt delete bowler
	}

	fun editStatisticsWidget() {
		// TODO: Navigate to statistics widget editor
	}
}