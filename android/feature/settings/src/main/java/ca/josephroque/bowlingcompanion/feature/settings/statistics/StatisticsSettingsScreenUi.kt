package ca.josephroque.bowlingcompanion.feature.settings.statistics

import ca.josephroque.bowlingcompanion.feature.settings.ui.statistics.StatisticsSettingsUiAction
import ca.josephroque.bowlingcompanion.feature.settings.ui.statistics.StatisticsSettingsUiState

sealed interface StatisticsSettingsScreenUiState {
	data object Loading : StatisticsSettingsScreenUiState

	data class Loaded(
		val statisticsSettings: StatisticsSettingsUiState,
	) : StatisticsSettingsScreenUiState
}

sealed interface StatisticsSettingsScreenUiAction {
	data class StatisticsSettingsAction(
		val action: StatisticsSettingsUiAction,
	) : StatisticsSettingsScreenUiAction
}

sealed interface StatisticsSettingsScreenEvent {
	data object Dismissed : StatisticsSettingsScreenEvent
}
