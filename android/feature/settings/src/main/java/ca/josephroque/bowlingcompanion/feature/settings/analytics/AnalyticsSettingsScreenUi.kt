package ca.josephroque.bowlingcompanion.feature.settings.analytics

import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.analytics.AnalyticsSettingsUiState

sealed interface AnalyticsSettingsScreenUiState {
	data object Loading : AnalyticsSettingsScreenUiState

	data class Loaded(
		val analyticsSettings: AnalyticsSettingsUiState,
	) : AnalyticsSettingsScreenUiState
}

sealed interface AnalyticsSettingsScreenUiAction {
	data class AnalyticsSettingsAction(
		val value: AnalyticsSettingsUiAction,
	) : AnalyticsSettingsScreenUiAction
}

sealed interface AnalyticsSettingsScreenEvent {
	data object Dismissed : AnalyticsSettingsScreenEvent
}
