package ca.josephroque.bowlingcompanion.feature.teamform

import ca.josephroque.bowlingcompanion.core.model.TeamUpdate
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormUiAction
import ca.josephroque.bowlingcompanion.feature.teamform.ui.TeamFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

sealed interface TeamFormScreenUiState {
	fun hasAnyChanges(): Boolean
	fun isSavable(): Boolean

	data object Loading : TeamFormScreenUiState {
		override fun hasAnyChanges(): Boolean = false
		override fun isSavable(): Boolean = false
	}

	data class Create(
		val form: TeamFormUiState,
		val topBar: TeamFormTopBarUiState,
	) : TeamFormScreenUiState {
		override fun isSavable(): Boolean = form.name.isNotBlank()

		override fun hasAnyChanges(): Boolean = form != TeamFormUiState()
	}

	data class Edit(
		val initialValue: TeamUpdate,
		val form: TeamFormUiState,
		val topBar: TeamFormTopBarUiState,
	) : TeamFormScreenUiState {
		override fun isSavable(): Boolean =
			form.name.isNotBlank() && form.updatedModel(existing = initialValue) != initialValue

		override fun hasAnyChanges(): Boolean = form.updatedModel(existing = initialValue) != initialValue
	}
}

fun TeamFormUiState.updatedModel(existing: TeamUpdate): TeamUpdate = TeamUpdate(
	id = existing.id,
	name = name,
	members = members
)

sealed interface TeamFormScreenUiAction {
	data object LoadTeam : TeamFormScreenUiAction

	data class MembersUpdated(val members: List<UUID>) : TeamFormScreenUiAction
	data class TeamForm(val action: TeamFormUiAction) : TeamFormScreenUiAction
}

sealed interface TeamFormScreenEvent {
	data object Dismissed : TeamFormScreenEvent

	data class ManageTeamMembers(val existingMembers: List<UUID>) : TeamFormScreenEvent
}

fun MutableStateFlow<TeamFormScreenUiState>.updateForm(
	function: (TeamFormUiState) -> TeamFormUiState,
) {
	this.update { state ->
		when (state) {
			TeamFormScreenUiState.Loading -> state
			is TeamFormScreenUiState.Create -> state.copy(form = function(state.form))
			is TeamFormScreenUiState.Edit -> state.copy(form = function(state.form))
		}
	}
}