package ca.josephroque.bowlingcompanion.feature.sharing

import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingData
import ca.josephroque.bowlingcompanion.feature.sharing.ui.SharingSource
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiAction
import ca.josephroque.bowlingcompanion.feature.sharing.ui.series.SeriesSharingConfigurationUiState

sealed interface SharingScreenUiState {
	data object Loading : SharingScreenUiState

	data class SharingSeries(
		val seriesSharing: SeriesSharingConfigurationUiState,
		val series: SharingData.Series
	) : SharingScreenUiState

	data object SharingGame : SharingScreenUiState
	data object SharingStatistic : SharingScreenUiState

	val sharingData: SharingData?
		get() = when (this) {
			is SharingSeries -> series
			is SharingGame -> SharingData.Game
			is SharingStatistic -> SharingData.Statistic
			Loading -> null
		}
}

sealed interface SharingScreenUiAction {
	data object ShareButtonClicked : SharingScreenUiAction

	data class DidStartSharing(val source: SharingSource, val isSystemInDarkTheme: Boolean) : SharingScreenUiAction
	data class SeriesSharingAction(val action: SeriesSharingConfigurationUiAction) : SharingScreenUiAction
	data object GameSharingAction : SharingScreenUiAction
	data object StatisticSharingAction : SharingScreenUiAction
}

sealed interface SharingScreenEvent {
	data object Dismissed : SharingScreenEvent
}
