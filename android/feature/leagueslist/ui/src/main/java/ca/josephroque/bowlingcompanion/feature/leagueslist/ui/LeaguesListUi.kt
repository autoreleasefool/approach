package ca.josephroque.bowlingcompanion.feature.leagueslist.ui

import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence

data class LeaguesListUiState(
	val list: List<LeagueListItem>,
	val leagueToArchive: LeagueListItem?,
	val isShowingHeader: Boolean,
	val filter: Filter = Filter(),
) {
	data class Filter(val recurrence: LeagueRecurrence? = null) {
		val isEmpty: Boolean = recurrence == null
		val isNotEmpty: Boolean = !isEmpty
	}
}

sealed interface LeaguesListUiAction {
	data object AddLeagueClicked : LeaguesListUiAction

	data class LeagueClicked(val league: LeagueListItem) : LeaguesListUiAction
	data class LeagueEdited(val league: LeagueListItem) : LeaguesListUiAction
	data class LeagueArchived(val league: LeagueListItem) : LeaguesListUiAction
	data class RecurrenceClicked(val recurrence: LeagueRecurrence?) : LeaguesListUiAction

	data object ConfirmArchiveClicked : LeaguesListUiAction
	data object DismissArchiveClicked : LeaguesListUiAction
}
