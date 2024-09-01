package ca.josephroque.bowlingcompanion.feature.leaguedetails

import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.leaguedetails.ui.LeagueDetailsUiState

sealed interface LeagueDetailsScreenUiState {
	data object Loading : LeagueDetailsScreenUiState

	data class Loaded(val leagueDetails: LeagueDetailsUiState) : LeagueDetailsScreenUiState
}

sealed interface LeagueDetailsScreenUiAction {
	data class SeriesAdded(val seriesId: SeriesID) : LeagueDetailsScreenUiAction
	data class LeagueDetails(val action: LeagueDetailsUiAction) : LeagueDetailsScreenUiAction
}

sealed interface LeagueDetailsScreenEvent {
	data object Dismissed : LeagueDetailsScreenEvent

	data class AddSeries(val leagueId: LeagueID) : LeagueDetailsScreenEvent
	data class EditSeries(val seriesId: SeriesID) : LeagueDetailsScreenEvent
	data class ShowSeriesDetails(val seriesId: SeriesID) : LeagueDetailsScreenEvent
	data class UsePreBowl(val leagueId: LeagueID) : LeagueDetailsScreenEvent
}
