package ca.josephroque.bowlingcompanion.feature.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.feature.bowlerslist.BowlersListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(): ViewModel() {
	private val _bowlersListState: MutableStateFlow<BowlersListUiState> = MutableStateFlow(BowlersListUiState.Loading)
	val bowlersListState: StateFlow<BowlersListUiState> = _bowlersListState
		.asStateFlow()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = BowlersListUiState.Loading,
		)

	fun loadBowlers() {
		viewModelScope.launch {
			_bowlersListState.update { BowlersListUiState.Success(emptyList()) }
		}
	}

	fun navigateToBowler(id: UUID) {
		// TODO: navigate to bowler
	}
}