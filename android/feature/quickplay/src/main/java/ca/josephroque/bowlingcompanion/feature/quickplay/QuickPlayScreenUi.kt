package ca.josephroque.bowlingcompanion.feature.quickplay

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayTopBarUiState
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayUiAction
import ca.josephroque.bowlingcompanion.feature.quickplay.ui.QuickPlayUiState

sealed interface QuickPlayScreenUiState {
	data object Loading : QuickPlayScreenUiState

	data class Loaded(
		val quickPlay: QuickPlayUiState = QuickPlayUiState(),
		val topBar: QuickPlayTopBarUiState = QuickPlayTopBarUiState(),
	) : QuickPlayScreenUiState
}

sealed interface QuickPlayScreenUiAction {
	data object DidAppear : QuickPlayScreenUiAction

	data class AddedBowler(val bowlerId: BowlerID?) : QuickPlayScreenUiAction
	data class EditedLeague(val bowlerId: BowlerID, val leagueId: LeagueID?) : QuickPlayScreenUiAction

	data class QuickPlay(val action: QuickPlayUiAction) : QuickPlayScreenUiAction
	data class TopBar(val action: QuickPlayTopBarUiAction) : QuickPlayScreenUiAction
}

sealed interface QuickPlayScreenEvent {
	data object Dismissed : QuickPlayScreenEvent
	data object ShowHowToUseQuickPlay : QuickPlayScreenEvent

	data class BeganRecording(val seriesIds: List<SeriesID>, val initialGameId: GameID) :
		QuickPlayScreenEvent
	data class AddBowler(val existingBowlers: Set<BowlerID>) : QuickPlayScreenEvent
	data class EditLeague(val bowlerId: BowlerID, val leagueId: LeagueID?) : QuickPlayScreenEvent
}
