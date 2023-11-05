package ca.josephroque.bowlingcompanion.feature.alleyslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.feature.alleyslist.ui.AlleysListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlleysListViewModel @Inject constructor(
	alleysRepository: AlleysRepository,
): ViewModel() {
	val alleysListState: StateFlow<AlleysListUiState> =
		alleysRepository.getAlleysList()
			.map(AlleysListUiState::Success)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = AlleysListUiState.Loading,
			)
}