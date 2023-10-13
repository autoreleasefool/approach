package ca.josephroque.bowlingcompanion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
	private val analyticsClient: AnalyticsClient,
	userDataRepository: UserDataRepository,
) : ViewModel() {
	private val isLaunchComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)

	val mainActivityUiState = combine(
		userDataRepository.userData,
		isLaunchComplete,
	) { userData, isLaunchComplete ->
		MainActivityUiState.Success(
			isOnboardingComplete = userData.isOnboardingComplete,
			isLaunchComplete = isLaunchComplete,
		)
	}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = MainActivityUiState.Loading,
		)

	fun didFirstLaunch() {
		if (isLaunchComplete.value) return

		analyticsClient.initialize()

		isLaunchComplete.value = true
	}
}

sealed interface MainActivityUiState {
	data object Loading: MainActivityUiState
	data class Success(
		val isOnboardingComplete: Boolean,
		val isLaunchComplete: Boolean,
	): MainActivityUiState
}

fun MainActivityUiState.isLaunchComplete(): Boolean = when (this) {
	MainActivityUiState.Loading -> false
	is MainActivityUiState.Success -> this.isLaunchComplete
}