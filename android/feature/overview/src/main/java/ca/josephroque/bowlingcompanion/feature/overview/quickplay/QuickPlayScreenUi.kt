package ca.josephroque.bowlingcompanion.feature.overview.quickplay

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayUiState
import java.util.UUID

sealed interface QuickPlayScreenUiState {
	data object Loading : QuickPlayScreenUiState

	data class Loaded(val quickPlay: QuickPlayUiState = QuickPlayUiState()) : QuickPlayScreenUiState
}

sealed interface QuickPlayScreenUiAction {
	data object DidAppear : QuickPlayScreenUiAction

	data class AddedBowler(val bowlerId: BowlerID?) : QuickPlayScreenUiAction
	data class EditedLeague(val bowlerId: BowlerID, val leagueId: LeagueID?) : QuickPlayScreenUiAction

	data class QuickPlay(val action: QuickPlayUiAction) : QuickPlayScreenUiAction
}

sealed interface QuickPlayScreenEvent {
	data object Dismissed : QuickPlayScreenEvent
	data object ShowHowToUseQuickPlay : QuickPlayScreenEvent

	data class BeganRecording(val seriesIds: List<UUID>, val initialGameId: UUID) :
		QuickPlayScreenEvent
	data class AddBowler(val existingBowlers: Set<BowlerID>) : QuickPlayScreenEvent
	data class EditLeague(val bowlerId: BowlerID, val leagueId: LeagueID?) : QuickPlayScreenEvent
}
