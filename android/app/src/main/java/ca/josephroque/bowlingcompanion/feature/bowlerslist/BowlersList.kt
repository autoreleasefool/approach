package ca.josephroque.bowlingcompanion.feature.bowlerslist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import java.util.UUID

fun LazyListScope.bowlersList(
	bowlersListState: BowlersListUiState,
	onBowlerClick: (UUID) -> Unit,
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
					modifier = Modifier
						.padding(horizontal = 16.dp)
						.padding(bottom = 16.dp),
				)
			}
		}
	}
}

sealed interface BowlersListUiState {

	data object Loading: BowlersListUiState

	data class Success(
		val list: List<BowlerListItem>
	): BowlersListUiState
}