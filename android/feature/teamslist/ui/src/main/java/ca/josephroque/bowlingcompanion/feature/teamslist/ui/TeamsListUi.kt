package ca.josephroque.bowlingcompanion.feature.teamslist.ui

import ca.josephroque.bowlingcompanion.core.model.TeamListItem

data class TeamsListUiState(val list: List<TeamListItem>, val teamToDelete: TeamListItem?)

sealed interface TeamsListUiAction {
	data object AddTeamClicked : TeamsListUiAction

	data class TeamClicked(val team: TeamListItem) : TeamsListUiAction
	data class TeamEdited(val team: TeamListItem) : TeamsListUiAction
	data class TeamDeleted(val team: TeamListItem) : TeamsListUiAction

	data object ConfirmDeleteClicked : TeamsListUiAction
	data object DismissDeleteClicked : TeamsListUiAction
}
