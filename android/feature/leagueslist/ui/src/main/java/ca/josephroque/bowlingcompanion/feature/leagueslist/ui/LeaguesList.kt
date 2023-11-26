package ca.josephroque.bowlingcompanion.feature.leagueslist.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import ca.josephroque.bowlingcompanion.core.model.ui.LeagueRow
import java.util.UUID

fun LazyListScope.leaguesList(
	leaguesListState: LeaguesListUiState,
	onLeagueClick: (UUID) -> Unit,
) {
	when (leaguesListState) {
		LeaguesListUiState.Loading -> Unit
		is LeaguesListUiState.Success -> {
			items(
				items = leaguesListState.list,
				key = { it.id },
			) { league ->
				LeagueRow(
					name = league.name,
					recurrence = league.recurrence,
					lastSeriesDate = league.lastSeriesDate,
					average = league.average,
					onClick = { onLeagueClick(league.id) },
				)
			}
		}
	}
}

sealed interface LeaguesListUiState {
	data object Loading: LeaguesListUiState
	data class Success(
		val list: List<LeagueListItem>
	): LeaguesListUiState
}