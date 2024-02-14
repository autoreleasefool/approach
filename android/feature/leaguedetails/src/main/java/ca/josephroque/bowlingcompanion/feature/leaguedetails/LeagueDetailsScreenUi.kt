package ca.josephroque.bowlingcompanion.feature.leaguedetails

import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiState
import java.util.UUID

sealed interface LeagueDetailsScreenUiState {
	data object Loading : LeagueDetailsScreenUiState

	data class Loaded(
		val leagueDetails: LeagueDetailsUiState,
	) : LeagueDetailsScreenUiState
}

sealed interface LeagueDetailsScreenUiAction {
	data class SeriesAdded(val seriesId: UUID) : LeagueDetailsScreenUiAction
	data class LeagueDetails(val action: LeagueDetailsUiAction) : LeagueDetailsScreenUiAction
}

sealed interface LeagueDetailsScreenEvent {
	data object Dismissed : LeagueDetailsScreenEvent

	data class AddSeries(val leagueId: UUID) : LeagueDetailsScreenEvent
	data class EditSeries(val seriesId: UUID) : LeagueDetailsScreenEvent
	data class ShowSeriesDetails(val seriesId: UUID) : LeagueDetailsScreenEvent
}
