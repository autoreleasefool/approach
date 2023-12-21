package ca.josephroque.bowlingcompanion.feature.seriesform

import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiState
import java.util.UUID

sealed interface SeriesFormScreenUiState {
	data object Loading: SeriesFormScreenUiState

	data class Create(
		val form: SeriesFormUiState,
		val topBar: SeriesFormTopBarUiState,
	): SeriesFormScreenUiState

	data class Edit(
		val initialValue: SeriesUpdate,
		val form: SeriesFormUiState,
		val topBar: SeriesFormTopBarUiState,
	): SeriesFormScreenUiState
}

fun SeriesFormUiState.updatedModel(existing: SeriesUpdate): SeriesUpdate = existing.copy(
	date = date,
	preBowl = preBowl,
	excludeFromStatistics = excludeFromStatistics,
	alleyId = alley?.id,
)

sealed interface SeriesFormScreenUiAction {
	data object LoadSeries: SeriesFormScreenUiAction
	data class AlleyUpdated(val alleyId: UUID?): SeriesFormScreenUiAction

	data class SeriesForm(
		val action: SeriesFormUiAction,
	): SeriesFormScreenUiAction
}

sealed interface SeriesFormScreenEvent {
	data class Dismissed(val id: UUID?): SeriesFormScreenEvent
	data class EditAlley(val alleyId: UUID?): SeriesFormScreenEvent
}