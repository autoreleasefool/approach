package ca.josephroque.bowlingcompanion.feature.statisticsoverview.sourcepicker

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableFilter
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerTopBarUiState
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiAction
import ca.josephroque.bowlingcompanion.feature.statisticsoverview.ui.sourcepicker.SourcePickerUiState

sealed interface SourcePickerScreenUiState {
	data object Loading : SourcePickerScreenUiState

	data class Loaded(val sourcePicker: SourcePickerUiState, val topBar: SourcePickerTopBarUiState) :
		SourcePickerScreenUiState
}

sealed interface SourcePickerScreenUiAction {
	data object DidAppear : SourcePickerScreenUiAction

	data class UpdatedBowler(val bowler: BowlerID?) : SourcePickerScreenUiAction
	data class UpdatedLeague(val league: LeagueID?) : SourcePickerScreenUiAction
	data class UpdatedSeries(val series: SeriesID?) : SourcePickerScreenUiAction
	data class UpdatedGame(val game: GameID?) : SourcePickerScreenUiAction

	data class SourcePicker(val action: SourcePickerUiAction) : SourcePickerScreenUiAction
}

sealed interface SourcePickerScreenEvent {
	data object Dismissed : SourcePickerScreenEvent

	data class ShowStatistics(val filter: TrackableFilter) : SourcePickerScreenEvent
	data class EditBowler(val bowler: BowlerID?) : SourcePickerScreenEvent
	data class EditLeague(val bowler: BowlerID, val league: LeagueID?) : SourcePickerScreenEvent
	data class EditSeries(val league: LeagueID, val series: SeriesID?) : SourcePickerScreenEvent
	data class EditGame(val series: SeriesID, val game: GameID?) : SourcePickerScreenEvent
}
