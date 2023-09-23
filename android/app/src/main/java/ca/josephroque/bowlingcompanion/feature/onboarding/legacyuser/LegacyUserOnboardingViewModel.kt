package ca.josephroque.bowlingcompanion.feature.onboarding.legacyuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.MigrationManager
import ca.josephroque.bowlingcompanion.core.database.legacy.migration.MigrationStep
import ca.josephroque.bowlingcompanion.core.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.dispatcher.Dispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LegacyUserOnboardingViewModel @Inject constructor(
	private val migrationManager: MigrationManager,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
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
			withContext(ioDispatcher) {
				migrationManager.beginMigration()
			}
		}

		viewModelScope.launch {
			withContext(ioDispatcher) {
				migrationManager.currentStep
					.takeWhile { it != null }
					.collect { currentStep ->
						_uiState.value = LegacyUserOnboardingUiState.ImportingData(currentStep)
					}
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
