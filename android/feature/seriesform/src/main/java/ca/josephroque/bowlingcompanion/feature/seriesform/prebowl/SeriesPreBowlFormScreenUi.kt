package ca.josephroque.bowlingcompanion.feature.seriesform.prebowl

import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.prebowl.SeriesPreBowlFormUiState
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface SeriesPreBowlFormScreenUiState {
	data object Loading : SeriesPreBowlFormScreenUiState
	data class Loaded(
		val topBar: SeriesPreBowlFormTopBarUiState,
		val form: SeriesPreBowlFormUiState,
	) : SeriesPreBowlFormScreenUiState
}

sealed interface SeriesPreBowlFormScreenUiAction {
	data class SeriesUpdated(val seriesId: UUID?) : SeriesPreBowlFormScreenUiAction
	data class Form(val action: SeriesPreBowlFormUiAction) : SeriesPreBowlFormScreenUiAction
	data class TopBar(val action: SeriesPreBowlFormTopBarUiAction) : SeriesPreBowlFormScreenUiAction
}

sealed interface SeriesPreBowlFormScreenEvent {
	data object Dismissed : SeriesPreBowlFormScreenEvent
	data class ShowSeriesPicker(val leagueId: UUID, val seriesId: UUID?) : SeriesPreBowlFormScreenEvent
}

fun MutableStateFlow<SeriesPreBowlFormScreenUiState>.updateForm(
	function: (SeriesPreBowlFormScreenUiState.Loaded) -> SeriesPreBowlFormScreenUiState.Loaded,
) {
	this.update { state ->
		when (state) {
			SeriesPreBowlFormScreenUiState.Loading -> state
			is SeriesPreBowlFormScreenUiState.Loaded -> function(state)
		}
	}
}
