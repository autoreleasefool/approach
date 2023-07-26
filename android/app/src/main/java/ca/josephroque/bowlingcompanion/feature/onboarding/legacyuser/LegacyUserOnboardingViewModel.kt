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
				showApproachHeader()
			LegacyUserOnboardingUiEvent.ApproachHeaderAnimationFinished ->
				showApproachDetails()
			LegacyUserOnboardingUiEvent.GetStartedClicked ->
				startDataImport()
		}
	}

	private fun showApproachHeader() {
		_uiState.value = LegacyUserOnboardingUiState.ShowingApproachHeader(isDetailsVisible = false)
	}

	private fun showApproachDetails() {
		_uiState.value = LegacyUserOnboardingUiState.ShowingApproachHeader(isDetailsVisible = true)
	}

	private fun startDataImport() {
		if (_uiState.value == LegacyUserOnboardingUiState.ImportingData) {
			return
		}

		_uiState.value = LegacyUserOnboardingUiState.ImportingData

		// TODO: start importing data
	}
}

sealed interface LegacyUserOnboardingUiState {
	object Started: LegacyUserOnboardingUiState
	data class ShowingApproachHeader(
		val isDetailsVisible: Boolean
	): LegacyUserOnboardingUiState
	object ImportingData: LegacyUserOnboardingUiState
	object Complete: LegacyUserOnboardingUiState
}

sealed interface LegacyUserOnboardingUiEvent {
	object ApproachIconClicked: LegacyUserOnboardingUiEvent
	object ApproachHeaderAnimationFinished: LegacyUserOnboardingUiEvent
	object GetStartedClicked: LegacyUserOnboardingUiEvent
}