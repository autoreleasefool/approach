package ca.josephroque.bowlingcompanion.feature.seriesdetails

import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.seriesdetails.ui.SeriesDetailsUiState

sealed interface SeriesDetailsScreenUiState {
	data object Loading: SeriesDetailsScreenUiState

	data class Loaded(
		val seriesDetails: SeriesDetailsUiState,
	): SeriesDetailsScreenUiState
}

sealed interface SeriesDetailsScreenUiAction {
	data class SeriesDetails(val action: SeriesDetailsUiAction): SeriesDetailsScreenUiAction
}

sealed interface SeriesDetailsScreenEvent {
	data object Dismissed: SeriesDetailsScreenEvent

	data class EditGame(val args: EditGameArgs): SeriesDetailsScreenEvent
}