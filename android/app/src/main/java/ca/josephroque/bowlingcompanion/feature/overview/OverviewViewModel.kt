package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.models.sample.SampleData
import ca.josephroque.bowlingcompanion.feature.overview.bowlerslist.BowlersListUiState
import ca.josephroque.bowlingcompanion.feature.overview.bowlerslist.BowlersListUiState.Loading
import ca.josephroque.bowlingcompanion.feature.overview.bowlerslist.BowlersListUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(): ViewModel() {

	private val _bowlersListState: MutableStateFlow<BowlersListUiState> = MutableStateFlow(Loading)
	val bowlersListState: StateFlow<BowlersListUiState> = _bowlersListState
		.asStateFlow()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = Loading
		)

	fun loadBowlers() {
		viewModelScope.launch {
			_bowlersListState.update { Success(SampleData.sampleBowlers) }
		}
	}

	fun navigateToBowler(id: UUID) {

	}
}