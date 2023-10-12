package ca.josephroque.bowlingcompanion.feature.leagueslist

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
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
				contentType = { "league" },
			) { league ->
				LeagueItemRow(
					league = league,
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