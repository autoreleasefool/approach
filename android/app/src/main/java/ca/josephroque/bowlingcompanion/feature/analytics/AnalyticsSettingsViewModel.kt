package ca.josephroque.bowlingcompanion.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.analytics.toggle
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsSettingsViewModel @Inject constructor(
	private val userDataRepository: UserDataRepository,
): ViewModel() {

	val uiState: StateFlow<AnalyticsSettingsUiState> = userDataRepository.userData.map {
		AnalyticsSettingsUiState.Success(analyticsOptInStatus = it.analyticsOptIn)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = AnalyticsSettingsUiState.Loading,
	)

	fun toggleAnalyticsOptInStatus(value: Boolean?) {
		viewModelScope.launch {
			val status = userDataRepository.userData.first().analyticsOptIn
			val updatedStatus = when (value) {
				null -> status.toggle()
				true -> AnalyticsOptInStatus.OPTED_IN
				false -> AnalyticsOptInStatus.OPTED_OUT
			}
			userDataRepository.setAnalyticsOptInStatus(updatedStatus)
		}
	}
}

sealed interface AnalyticsSettingsUiState {
	data object Loading: AnalyticsSettingsUiState
	data class Success(
		val analyticsOptInStatus: AnalyticsOptInStatus,
	): AnalyticsSettingsUiState
}