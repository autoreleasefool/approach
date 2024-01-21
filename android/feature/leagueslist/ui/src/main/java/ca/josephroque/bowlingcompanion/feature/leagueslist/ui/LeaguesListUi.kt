package ca.josephroque.bowlingcompanion.feature.leagueslist.ui

import ca.josephroque.bowlingcompanion.core.model.LeagueListItem

data class LeaguesListUiState(
	val list: List<LeagueListItem>,
	val leagueToArchive: LeagueListItem?,
	val isShowingHeader: Boolean,
)

sealed interface LeaguesListUiAction {
	data object AddLeagueClicked: LeaguesListUiAction

	data class LeagueClicked(val league: LeagueListItem): LeaguesListUiAction
	data class LeagueEdited(val league: LeagueListItem): LeaguesListUiAction
	data class LeagueArchived(val league: LeagueListItem): LeaguesListUiAction

	data object ConfirmArchiveClicked: LeaguesListUiAction
	data object DismissArchiveClicked: LeaguesListUiAction
}