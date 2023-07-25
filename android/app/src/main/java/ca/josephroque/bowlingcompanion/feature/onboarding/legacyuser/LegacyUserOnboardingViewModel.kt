package ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LegacyUserOnboardingViewModel @Inject constructor(): ViewModel() {

	private val _uiState: MutableStateFlow<LegacyUserOnboardingUiState> =
		MutableStateFlow(LegacyUserOnboardingUiState.Started)
	val uiState = _uiState.asStateFlow()

	fun handleEvent(event: LegacyUserOnboardingUiEvent) {
		when (event) {
			LegacyUserOnboardingUiEvent.ApproachIconClicked ->
				showApproachDetails()
			LegacyUserOnboardingUiEvent.GetStartedClicked ->
				startDataImport()
		}
	}

	private fun showApproachDetails() {
		_uiState.value = LegacyUserOnboardingUiState.ShowingApproachDetails
	}

	private fun startDataImport() {
		when (_uiState.value) {
			LegacyUserOnboardingUiState.Started,
			LegacyUserOnboardingUiState.ImportingData,
			LegacyUserOnboardingUiState.Complete -> return
			LegacyUserOnboardingUiState.ShowingApproachDetails -> Unit
		}

		// TODO: start importing data
	}
}

sealed interface LegacyUserOnboardingUiState {
	object Started: LegacyUserOnboardingUiState
	object ShowingApproachDetails: LegacyUserOnboardingUiState
	object ImportingData: LegacyUserOnboardingUiState
	object Complete: LegacyUserOnboardingUiState
}

sealed interface LegacyUserOnboardingUiEvent {
	object ApproachIconClicked: LegacyUserOnboardingUiEvent
	object GetStartedClicked: LegacyUserOnboardingUiEvent
}