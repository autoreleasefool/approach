package ca.josephroque.bowlingcompanion.feature.statisticsoverview.sourcepicker

import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiState
import java.util.UUID

sealed interface SourcePickerScreenUiState {
	data object Loading: SourcePickerScreenUiState

	data class Loaded(
		val sourcePicker: SourcePickerUiState,
	): SourcePickerScreenUiState
}

sealed interface SourcePickerScreenUiAction {
	data object DidAppear: SourcePickerScreenUiAction

	data class UpdatedBowler(val bowler: UUID?): SourcePickerScreenUiAction
	data class UpdatedLeague(val league: UUID?): SourcePickerScreenUiAction
	data class UpdatedSeries(val series: UUID?): SourcePickerScreenUiAction
	data class UpdatedGame(val game: UUID?): SourcePickerScreenUiAction

	data class SourcePicker(val action: SourcePickerUiAction): SourcePickerScreenUiAction
}

sealed interface SourcePickerScreenEvent {
	data object Dismissed: SourcePickerScreenEvent

	data class ShowStatistics(val filter: TrackableFilter): SourcePickerScreenEvent
	data class EditBowler(val bowler: UUID?): SourcePickerScreenEvent
	data class EditLeague(val bowler: UUID, val league: UUID?): SourcePickerScreenEvent
	data class EditSeries(val league: UUID, val series: UUID?): SourcePickerScreenEvent
	data class EditGame(val series: UUID, val game: UUID?): SourcePickerScreenEvent
}