package ca.josephroque.bowlingcompanion.feature.bowlerdetails

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsUiState
import java.util.UUID

sealed interface BowlerDetailsScreenUiState {
	data object Loading : BowlerDetailsScreenUiState

	data class Loaded(val bowler: BowlerDetailsUiState) : BowlerDetailsScreenUiState
}

sealed interface BowlerDetailsScreenUiAction {
	data class PreferredGearSelected(val gear: Set<UUID>) : BowlerDetailsScreenUiAction
	data class BowlerDetailsAction(val action: BowlerDetailsUiAction) : BowlerDetailsScreenUiAction
}

sealed interface BowlerDetailsScreenEvent {
	data object Dismissed : BowlerDetailsScreenEvent

	data class EditStatisticsWidget(val context: String, val bowlerId: BowlerID) :
		BowlerDetailsScreenEvent
	data class ShowPreferredGearPicker(val selectedGear: Set<UUID>) : BowlerDetailsScreenEvent
	data class EditLeague(val leagueId: LeagueID) : BowlerDetailsScreenEvent
	data class AddLeague(val bowlerId: BowlerID) : BowlerDetailsScreenEvent
	data class ShowGearDetails(val gearId: UUID) : BowlerDetailsScreenEvent
	data class ShowLeagueDetails(val leagueId: LeagueID) : BowlerDetailsScreenEvent
	data class ShowEventDetails(val leagueId: LeagueID) : BowlerDetailsScreenEvent
	data class ShowWidgetStatistics(val filter: TrackableFilter) : BowlerDetailsScreenEvent
	data object ShowWidgetNotEnoughDataError : BowlerDetailsScreenEvent
	data object ShowWidgetUnavailableError : BowlerDetailsScreenEvent
}
