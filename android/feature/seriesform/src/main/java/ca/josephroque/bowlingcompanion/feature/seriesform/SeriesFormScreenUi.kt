package ca.josephroque.bowlingcompanion.feature.seriesform

import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiAction
import ca.josephroque.bowlingcompanion.feature.seriesform.ui.SeriesFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface SeriesFormScreenUiState {
	fun hasAnyChanges(): Boolean
	fun isSavable(): Boolean

	data object Loading : SeriesFormScreenUiState {
		override fun hasAnyChanges(): Boolean = false
		override fun isSavable(): Boolean = false
	}

	data class TeamCreate(val form: SeriesFormUiState, val topBar: SeriesFormTopBarUiState) :
		SeriesFormScreenUiState {
		override fun isSavable(): Boolean = true
		override fun hasAnyChanges(): Boolean = true
	}

	data class Create(val form: SeriesFormUiState, val topBar: SeriesFormTopBarUiState) :
		SeriesFormScreenUiState {
		override fun isSavable(): Boolean = true
		override fun hasAnyChanges(): Boolean = true
	}

	data class Edit(
		val initialValue: SeriesUpdate,
		val form: SeriesFormUiState,
		val topBar: SeriesFormTopBarUiState,
	) : SeriesFormScreenUiState {
		override fun isSavable(): Boolean = form.updatedModel(existing = initialValue) != initialValue

		override fun hasAnyChanges(): Boolean =
			form.updatedModel(existing = initialValue) != initialValue || form.hasLeagueChanged
	}
}

fun SeriesFormUiState.updatedModel(existing: SeriesUpdate): SeriesUpdate = existing.copy(
	date = date,
	preBowl = preBowl,
	appliedDate = if (isUsingPreBowl) appliedDate else null,
	excludeFromStatistics = if (preBowl == SeriesPreBowl.PRE_BOWL && !isUsingPreBowl) {
		ExcludeFromStatistics.EXCLUDE
	} else {
		excludeFromStatistics
	},
	alleyId = alley?.id,
)

sealed interface SeriesFormScreenUiAction {
	data object LoadSeries : SeriesFormScreenUiAction
	data class AlleyUpdated(val alleyId: AlleyID?) : SeriesFormScreenUiAction
	data class LeagueUpdated(val leagueId: LeagueID?) : SeriesFormScreenUiAction

	data class SeriesForm(val action: SeriesFormUiAction) : SeriesFormScreenUiAction
}

sealed interface SeriesFormScreenEvent {
	data class Dismissed(val seriesId: SeriesID?) : SeriesFormScreenEvent
	data class EditAlley(val alleyId: AlleyID?) : SeriesFormScreenEvent
	data class EditLeague(val bowlerId: BowlerID, val leagueId: LeagueID) : SeriesFormScreenEvent
	data class StartTeamSeries(val teamSeriesId: TeamSeriesID, val initialGameId: GameID) :
		SeriesFormScreenEvent
}

fun MutableStateFlow<SeriesFormScreenUiState>.updateForm(function: (SeriesFormUiState) -> SeriesFormUiState) {
	this.update { state ->
		when (state) {
			SeriesFormScreenUiState.Loading -> state
			is SeriesFormScreenUiState.TeamCreate -> {
				val updatedState = state.copy(form = function(state.form))
				updatedState.copy(
					topBar = updatedState.topBar.copy(isSaveButtonEnabled = updatedState.hasAnyChanges()),
				)
			}
			is SeriesFormScreenUiState.Create -> {
				val updatedState = state.copy(form = function(state.form))
				updatedState.copy(
					topBar = updatedState.topBar.copy(isSaveButtonEnabled = updatedState.hasAnyChanges()),
				)
			}
			is SeriesFormScreenUiState.Edit -> {
				val updatedState = state.copy(form = function(state.form))
				updatedState.copy(
					topBar = updatedState.topBar.copy(isSaveButtonEnabled = updatedState.hasAnyChanges()),
				)
			}
		}
	}
}
