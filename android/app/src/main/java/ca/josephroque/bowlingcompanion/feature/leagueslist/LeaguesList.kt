package ca.josephroque.bowlingcompanion.feature.leagueslist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.LeagueListItem
import java.util.UUID

fun LazyListScope.leaguesList(
	leaguesListState: LeaguesListUiState,
	onLeagueClick: (UUID) -> Unit,
) {
	item {
		Text(
			text = stringResource(R.string.league_list_title),
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
		)
	}

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