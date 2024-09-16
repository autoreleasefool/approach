package ca.josephroque.bowlingcompanion.feature.teamform.ui

import androidx.annotation.StringRes
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem

data class TeamFormUiState(
	val name: String = "",
	@StringRes val nameErrorId: Int? = null,
	val members: List<TeamMemberListItem> = emptyList(),
	@StringRes val membersErrorId: Int? = null,

	val isShowingDeleteDialog: Boolean = false,
	val isDeleteButtonEnabled: Boolean = false,
	val isShowingDiscardChangesDialog: Boolean = false,
)

sealed interface TeamFormUiAction {
	data object BackClicked : TeamFormUiAction
	data object DoneClicked : TeamFormUiAction

	data object DeleteClicked : TeamFormUiAction
	data object ConfirmDeleteClicked : TeamFormUiAction
	data object DismissDeleteClicked : TeamFormUiAction

	data object DiscardChangesClicked : TeamFormUiAction
	data object CancelDiscardChangesClicked : TeamFormUiAction

	data class NameChanged(val name: String) : TeamFormUiAction
	data class MemberMoved(val from: Int, val to: Int) : TeamFormUiAction

	data object ManageTeamMembersClicked : TeamFormUiAction
}

data class TeamFormTopBarUiState(
	val existingName: String? = null,
	val isSaveButtonEnabled: Boolean = false,
)
