package ca.josephroque.bowlingcompanion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
	userDataRepository: UserDataRepository,
) : ViewModel() {
	val mainActivityUiState = userDataRepository.userData.map {
		MainActivityUiState.Success(it.isOnboardingComplete)
	}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = MainActivityUiState.Loading,
		)
}

sealed interface MainActivityUiState {
	object Loading: MainActivityUiState
	data class Success(
		val isOnboardingComplete: Boolean
	): MainActivityUiState
}