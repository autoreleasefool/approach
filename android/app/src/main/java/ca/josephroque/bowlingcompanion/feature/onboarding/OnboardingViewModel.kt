package ca.josephroque.bowlingcompanion.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.filesystem.FileManager
import ca.josephroque.bowlingcompanion.core.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
	private val userDataRepository: UserDataRepository,
	fileManager: FileManager,
): ViewModel() {

	private val userDataUiState = userDataRepository.userData.map {
		UserDataUiState.Success(it)
	}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = UserDataUiState.Loading,
		)

	val uiState = userDataUiState.map {
		when (it) {
			is UserDataUiState.Loading -> Unit
			is UserDataUiState.Success -> {
				if (it.userData.isOnboardingComplete) {
					return@map OnboardingUiState.Completed
				}
			}
		}

		if (fileManager.fileExists(fileManager.legacyDatabaseFile)) {
			OnboardingUiState.LegacyUser
		} else {
			OnboardingUiState.NewUser
		}
	}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = OnboardingUiState.Loading,
		)

	fun didCompleteOnboarding() {
		viewModelScope.launch {
			userDataRepository.didCompleteOnboarding()
		}
	}
}

sealed interface OnboardingUiState {
	data object Loading: OnboardingUiState
	data object NewUser: OnboardingUiState
	data object LegacyUser: OnboardingUiState
	data object Completed: OnboardingUiState
}

sealed interface UserDataUiState {
	data object Loading: UserDataUiState
	data class Success(val userData: UserData): UserDataUiState
}
