package ca.josephroque.bowlingcompanion.feature.leagueform

import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.IncludeAdditionalPinFall
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiAction
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface LeagueFormScreenUiState {
	fun hasAnyChanges(): Boolean
	fun isSavable(): Boolean

	data object Loading : LeagueFormScreenUiState {
		override fun hasAnyChanges(): Boolean = false
		override fun isSavable(): Boolean = false
	}

	data class Create(val form: LeagueFormUiState, val topBar: LeagueFormTopBarUiState) :
		LeagueFormScreenUiState {
		override fun isSavable(): Boolean = form.name.isNotBlank()

		override fun hasAnyChanges(): Boolean = form != LeagueFormUiState(isEditing = form.isEditing)
	}

	data class Edit(
		val initialValue: LeagueUpdate,
		val form: LeagueFormUiState,
		val topBar: LeagueFormTopBarUiState,
	) : LeagueFormScreenUiState {
		override fun isSavable(): Boolean = form.name.isNotBlank() && form.updatedModel(id = initialValue.id) != initialValue

		override fun hasAnyChanges(): Boolean = form.updatedModel(id = initialValue.id) != initialValue
	}
}

fun LeagueFormUiState.updatedModel(id: LeagueID): LeagueUpdate = LeagueUpdate(
	id = id,
	name = name,
	additionalGames = when (includeAdditionalPinFall) {
		IncludeAdditionalPinFall.INCLUDE -> if (additionalGames > 0) additionalGames else null
		IncludeAdditionalPinFall.NONE -> null
	},
	additionalPinFall = when (includeAdditionalPinFall) {
		IncludeAdditionalPinFall.INCLUDE -> if (additionalGames > 0) additionalPinFall else null
		IncludeAdditionalPinFall.NONE -> null
	},
	excludeFromStatistics = excludeFromStatistics,
	numberOfGames = numberOfGames,
)

sealed interface LeagueFormScreenUiAction {
	data object LoadLeague : LeagueFormScreenUiAction
	data class LeagueForm(val action: LeagueFormUiAction) : LeagueFormScreenUiAction
}

sealed interface LeagueFormScreenEvent {
	data object Dismissed : LeagueFormScreenEvent
}

fun MutableStateFlow<LeagueFormScreenUiState>.updateForm(function: (LeagueFormUiState) -> LeagueFormUiState) {
	this.update { state ->
		when (state) {
			LeagueFormScreenUiState.Loading -> state
			is LeagueFormScreenUiState.Create -> {
				val updatedState = state.copy(form = function(state.form))
				updatedState.copy(
					topBar = updatedState.topBar.copy(isSaveButtonEnabled = updatedState.hasAnyChanges()),
				)
			}
			is LeagueFormScreenUiState.Edit -> {
				val updatedState = state.copy(form = function(state.form))
				updatedState.copy(
					topBar = updatedState.topBar.copy(isSaveButtonEnabled = updatedState.hasAnyChanges()),
				)
			}
		}
	}
}
