package ca.josephroque.bowlingcompanion.feature.overview.quickplay

import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayUiAction
import ca.josephroque.bowlingcompanion.feature.overview.ui.quickplay.QuickPlayUiState
import java.util.UUID

sealed interface QuickPlayScreenUiState {
	data object Loading : QuickPlayScreenUiState

	data class Loaded(
		val quickPlay: QuickPlayUiState = QuickPlayUiState(),
	): QuickPlayScreenUiState
}

sealed interface QuickPlayScreenUiAction {
	data object DidAppear: QuickPlayScreenUiAction

	data class AddedBowler(val bowlerId: UUID?): QuickPlayScreenUiAction
	data class EditedLeague(val bowlerId: UUID, val leagueId: UUID?): QuickPlayScreenUiAction

	data class QuickPlay(val action: QuickPlayUiAction): QuickPlayScreenUiAction
}

sealed interface QuickPlayScreenEvent {
	data object Dismissed: QuickPlayScreenEvent
	data object ShowHowToUseQuickPlay: QuickPlayScreenEvent

	data class BeganRecording(val bowlers: List<Pair<UUID, UUID>>): QuickPlayScreenEvent
	data class AddBowler(val existingBowlers: Set<UUID>): QuickPlayScreenEvent
	data class EditLeague(val bowlerId: UUID, val leagueId: UUID?): QuickPlayScreenEvent
}