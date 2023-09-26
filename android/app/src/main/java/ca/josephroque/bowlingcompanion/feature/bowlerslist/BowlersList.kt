package ca.josephroque.bowlingcompanion.feature.bowlerslist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import java.util.UUID

fun LazyListScope.bowlersList(
	bowlersListState: BowlersListUiState,
	onBowlerClick: (UUID) -> Unit,
) {
	item {
		Text(
			stringResource(R.string.bowler_list_title),
			fontSize = 24.sp,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
		)
	}

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
		val list: List<BowlerListItem>
	): BowlersListUiState
}