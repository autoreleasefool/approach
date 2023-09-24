package ca.josephroque.bowlingcompanion.feature.leagueslist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import java.util.UUID

fun LazyListScope.leaguesList(
	leaguesListState: LeaguesListUiState,
	onLeagueClick: (UUID) -> Unit,
) {
	when(leaguesListState) {
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
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
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