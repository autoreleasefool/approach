package ca.josephroque.bowlingcompanion.feature.overview.bowlerslist

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import ca.josephroque.bowlingcompanion.core.data.models.Bowler
import java.util.UUID

fun LazyListScope.bowlersList(
	bowlersListState: BowlersListUiState,
	onBowlerClick: (UUID) -> Unit
) {
	when (bowlersListState) {
		BowlersListUiState.Loading -> Unit
		is BowlersListUiState.Success -> {
			items(
				items = bowlersListState.list,
				key = { it.id },
				contentType = { "bowler" }
			) { bowler ->
				BowlerCard(
					bowler = bowler,
					onClick = { onBowlerClick(bowler.id) }
				)
			}
		}
	}
}

sealed interface BowlersListUiState {

	object Loading: BowlersListUiState

	data class Success(
		val list: List<Bowler>
	): BowlersListUiState
}