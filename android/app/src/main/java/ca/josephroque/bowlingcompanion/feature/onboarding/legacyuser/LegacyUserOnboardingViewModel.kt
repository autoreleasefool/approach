package ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.data.migration.MigrationManager
import ca.josephroque.bowlingcompanion.core.data.migration.MigrationStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LegacyUserOnboardingViewModel @Inject constructor(
	private val migrationManager: MigrationManager,
): ViewModel() {

	private val _uiState: MutableStateFlow<LegacyUserOnboardingUiState> =
		MutableStateFlow(LegacyUserOnboardingUiState.Started)
	val uiState = _uiState.asStateFlow()

	fun showApproachHeader() {
		_uiState.value = LegacyUserOnboardingUiState.ShowingApproachHeader(isDetailsVisible = false)
	}

	fun showApproachDetails() {
		_uiState.value = LegacyUserOnboardingUiState.ShowingApproachHeader(isDetailsVisible = true)
	}

	fun startDataImport() {
		if (_uiState.value is LegacyUserOnboardingUiState.ImportingData) {
			return
		}

		_uiState.value = LegacyUserOnboardingUiState.ImportingData(null)

		viewModelScope.launch {
			migrationManager.beginMigration()
		}

		viewModelScope.launch {
			migrationManager.currentStep
				.takeWhile { it != null }
				.collect { currentStep ->
					_uiState.value = LegacyUserOnboardingUiState.ImportingData(currentStep)
				}

			_uiState.value = LegacyUserOnboardingUiState.Complete
		}
	}
}

sealed interface LegacyUserOnboardingUiState {
	data object Started: LegacyUserOnboardingUiState
	data class ShowingApproachHeader(
		val isDetailsVisible: Boolean
	): LegacyUserOnboardingUiState
	data class ImportingData(
		val stepInProgress: MigrationStep?,
	): LegacyUserOnboardingUiState
	data object Complete: LegacyUserOnboardingUiState
}
