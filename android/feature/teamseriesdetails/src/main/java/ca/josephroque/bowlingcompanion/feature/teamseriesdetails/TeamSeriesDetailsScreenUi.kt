package ca.josephroque.bowlingcompanion.feature.teamseriesdetails

import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.teamseriesdetails.ui.TeamSeriesDetailsUiState

sealed interface TeamSeriesDetailsScreenUiState {
	data object Loading : TeamSeriesDetailsScreenUiState

	data class Loaded(
		val topBar: TeamSeriesDetailsTopBarUiState,
		val teamSeriesDetails: TeamSeriesDetailsUiState,
	) : TeamSeriesDetailsScreenUiState
}

sealed interface TeamSeriesDetailsScreenUiAction {
	data class TopBar(val action: TeamSeriesDetailsTopBarUiAction) : TeamSeriesDetailsScreenUiAction
	data class TeamSeriesDetails(val action: TeamSeriesDetailsUiAction) :
		TeamSeriesDetailsScreenUiAction
}

sealed interface TeamSeriesDetailsScreenEvent {
	data object Dismissed : TeamSeriesDetailsScreenEvent

	data class EditGame(val teamSeriesId: TeamSeriesID, val gameId: GameID) :
		TeamSeriesDetailsScreenEvent
}
