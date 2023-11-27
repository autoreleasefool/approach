package ca.josephroque.bowlingcompanion.feature.leagueslist.ui

import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import java.util.UUID

data class LeaguesListUiState(
	val list: List<LeagueListItem>,
	val leagueToArchive: LeagueListItem?,
)

sealed interface LeaguesListUiAction {
	data object AddLeagueClicked: LeaguesListUiAction

	data class LeagueClicked(val id: UUID): LeaguesListUiAction
	data class LeagueEdited(val id: UUID): LeaguesListUiAction
	data class LeagueArchived(val league: LeagueListItem): LeaguesListUiAction

	data object ConfirmArchiveClicked: LeaguesListUiAction
	data object DismissArchiveClicked: LeaguesListUiAction
}