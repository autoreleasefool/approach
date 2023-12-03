package ca.josephroque.bowlingcompanion.feature.bowlerdetails

import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsUiAction
import ca.josephroque.bowlingcompanion.feature.bowlerdetails.ui.BowlerDetailsUiState
import java.util.UUID

sealed interface BowlerDetailsScreenUiState {
	data object Loading: BowlerDetailsScreenUiState

	data class Loaded(
		val bowler: BowlerDetailsUiState,
	): BowlerDetailsScreenUiState
}

sealed interface BowlerDetailsScreenUiAction {
	data class PreferredGearSelected(val gear: Set<UUID>): BowlerDetailsScreenUiAction
	data class BowlerDetailsAction(val action: BowlerDetailsUiAction): BowlerDetailsScreenUiAction
}

sealed interface BowlerDetailsScreenEvent {
	data object Dismissed: BowlerDetailsScreenEvent

	data class EditStatisticsWidget(val context: String): BowlerDetailsScreenEvent
	data class ShowPreferredGearPicker(val selectedGear: Set<UUID>): BowlerDetailsScreenEvent
	data class EditLeague(val leagueId: UUID): BowlerDetailsScreenEvent
	data class AddLeague(val bowlerId: UUID): BowlerDetailsScreenEvent
	data class ShowGearDetails(val gearId: UUID): BowlerDetailsScreenEvent
	data class ShowLeagueDetails(val leagueId: UUID): BowlerDetailsScreenEvent
	data class ShowStatistics(val widget: UUID): BowlerDetailsScreenEvent
}