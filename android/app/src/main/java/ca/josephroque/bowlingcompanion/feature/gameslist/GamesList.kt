package ca.josephroque.bowlingcompanion.feature.gameslist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import java.util.UUID

fun LazyListScope.gamesList(
	gamesListState: GamesListUiState,
	onGameClick: (UUID) -> Unit,
) {
	when (gamesListState) {
		GamesListUiState.Loading -> Unit
		is GamesListUiState.Success -> {
			items(
				items = gamesListState.list,
				key = { it.id },
				contentType = { "games" },
			) { game ->
				GameItemRow(
					game = game,
					onClick = { onGameClick(game.id) },
					modifier = Modifier.padding(bottom = 16.dp),
				)
			}
		}
	}
}

sealed interface GamesListUiState {
	data object Loading: GamesListUiState
	data class Success(
		val list: List<GameListItem>,
	): GamesListUiState
}