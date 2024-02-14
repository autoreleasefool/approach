package ca.josephroque.bowlingcompanion.feature.settings.ui.analytics

import ca.josephroque.bowlingcompanion.core.model.AnalyticsOptInStatus

data class AnalyticsSettingsUiState(
	val analyticsOptInStatus: AnalyticsOptInStatus,
)

sealed interface AnalyticsSettingsUiAction {
	data object BackClicked : AnalyticsSettingsUiAction
	data class OptInStatusToggled(val value: Boolean) : AnalyticsSettingsUiAction
}
