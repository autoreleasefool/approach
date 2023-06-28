package ca.josephroque.bowlingcompanion

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
	private val _uiState = MutableStateFlow(MainActivityUiState.Success)
	val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()
}

sealed interface MainActivityUiState {
	object Success: MainActivityUiState
}