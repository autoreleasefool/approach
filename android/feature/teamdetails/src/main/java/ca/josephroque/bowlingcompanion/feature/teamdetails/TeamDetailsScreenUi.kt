package ca.josephroque.bowlingcompanion.feature.teamdetails

import ca.josephroque.bowlingcompanion.core.model.TeamID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsFloatingActionButtonUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.teamdetails.ui.TeamDetailsUiState

sealed interface TeamDetailsScreenUiState {
	data object Loading : TeamDetailsScreenUiState

	data class Loaded(val topBar: TeamDetailsTopBarUiState, val teamDetails: TeamDetailsUiState) :
		TeamDetailsScreenUiState
}

sealed interface TeamDetailsScreenUiAction {
	data class FloatingActionButton(val action: TeamDetailsFloatingActionButtonUiAction) :
		TeamDetailsScreenUiAction
	data class TopBar(val action: TeamDetailsTopBarUiAction) : TeamDetailsScreenUiAction
	data class TeamDetails(val action: TeamDetailsUiAction) : TeamDetailsScreenUiAction
}

sealed interface TeamDetailsScreenEvent {
	data object Dismissed : TeamDetailsScreenEvent

	data class AddSeries(val teamId: TeamID) : TeamDetailsScreenEvent
	data class EditSeries(val teamSeriesId: TeamSeriesID) : TeamDetailsScreenEvent
	data class ViewSeries(val teamSeriesId: TeamSeriesID) : TeamDetailsScreenEvent
}
