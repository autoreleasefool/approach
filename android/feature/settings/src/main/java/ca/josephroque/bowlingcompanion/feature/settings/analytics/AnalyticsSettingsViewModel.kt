package ca.josephroque.bowlingcompanion.feature.settings.analytics

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.toggle
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettingsUiState
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
): ApproachViewModel<AnalyticsSettingsScreenEvent>() {

	private val _analyticsSettings = userDataRepository.userData.map {
		AnalyticsSettingsUiState(analyticsOptInStatus = it.analyticsOptIn)
	}

	val uiState: StateFlow<AnalyticsSettingsScreenUiState> = _analyticsSettings.map {
		AnalyticsSettingsScreenUiState.Loaded(it)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = AnalyticsSettingsScreenUiState.Loading,
	)

	fun handleAction(action: AnalyticsSettingsScreenUiAction) {
		when (action) {
			is AnalyticsSettingsScreenUiAction.AnalyticsSettingsAction -> handleAnalyticsSettingsAction(action.value)
		}
	}

	private fun handleAnalyticsSettingsAction(action: AnalyticsSettingsUiAction) {
		when (action) {
			is AnalyticsSettingsUiAction.BackClicked -> sendEvent(AnalyticsSettingsScreenEvent.Dismissed)
			is AnalyticsSettingsUiAction.ToggleOptInStatus -> toggleAnalyticsOptInStatus(action.value)
		}
	}

	private fun toggleAnalyticsOptInStatus(value: Boolean?) {
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