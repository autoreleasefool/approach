package ca.josephroque.bowlingcompanion.feature.settings.analytics

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.analytics.AnalyticsClient
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AnalyticsSettingsViewModel @Inject constructor(
	private val analyticsClient: AnalyticsClient,
) : ApproachViewModel<AnalyticsSettingsScreenEvent>() {

	private val analyticsSettings = analyticsClient.optInStatus.map {
		AnalyticsSettingsUiState(analyticsOptInStatus = it)
	}

	val uiState: StateFlow<AnalyticsSettingsScreenUiState> = analyticsSettings.map {
		AnalyticsSettingsScreenUiState.Loaded(it)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = AnalyticsSettingsScreenUiState.Loading,
	)

	fun handleAction(action: AnalyticsSettingsScreenUiAction) {
		when (action) {
			is AnalyticsSettingsScreenUiAction.AnalyticsSettingsAction -> handleAnalyticsSettingsAction(
				action.value,
			)
		}
	}

	private fun handleAnalyticsSettingsAction(action: AnalyticsSettingsUiAction) {
		when (action) {
			is AnalyticsSettingsUiAction.BackClicked -> sendEvent(AnalyticsSettingsScreenEvent.Dismissed)
			is AnalyticsSettingsUiAction.OptInStatusToggled -> toggleAnalyticsOptInStatus(action.value)
		}
	}

	private fun toggleAnalyticsOptInStatus(value: Boolean) {
		viewModelScope.launch {
			analyticsClient.setOptInStatus(
				when (value) {
					true -> AnalyticsOptInStatus.OPTED_IN
					false -> AnalyticsOptInStatus.OPTED_OUT
				},
			)
		}
	}
}
