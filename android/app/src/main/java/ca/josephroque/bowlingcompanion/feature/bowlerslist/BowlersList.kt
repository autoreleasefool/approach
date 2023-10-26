package ca.josephroque.bowlingcompanion.feature.bowlerslist

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem

fun LazyListScope.bowlersList(
	bowlersListState: BowlersListUiState,
	onBowlerClick: (BowlerID) -> Unit,
) {
	when (bowlersListState) {
		BowlersListUiState.Loading -> Unit
		is BowlersListUiState.Success -> {
			items(
				items = bowlersListState.list,
				key = { it.id },
				contentType = { "bowler" },
			) { bowler ->
				BowlerItemRow(
					bowler = bowler,
					onClick = { onBowlerClick(bowler.id) },
				)
			}
		}
	}
}

sealed interface BowlersListUiState {
	data object Loading: BowlersListUiState
	data class Success(
		val list: List<BowlerListItem>,
	): BowlersListUiState
}