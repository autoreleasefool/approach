package ca.josephroque.bowlingcompanion.feature.settings.ui.analytics

import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus

data class AnalyticsSettingsUiState(
	val analyticsOptInStatus: AnalyticsOptInStatus,
)

sealed interface AnalyticsSettingsUiAction {
	data object BackClicked: AnalyticsSettingsUiAction
	data class ToggleOptInStatus(val value: Boolean?) : AnalyticsSettingsUiAction
}