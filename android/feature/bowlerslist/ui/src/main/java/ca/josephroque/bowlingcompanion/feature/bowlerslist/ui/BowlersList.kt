package ca.josephroque.bowlingcompanion.feature.bowlerslist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import java.util.UUID

@Composable
fun BowlerList(
	state: BowlersListUiState,
	onBowlerClick: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	modifier: Modifier = Modifier,
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	LazyColumn(modifier = modifier) {
		when (state) {
			BowlersListUiState.Loading -> {
				item {
					LoadingState()
				}
			}
			is BowlersListUiState.Success -> {
				if (state.list.isEmpty()) {
					item {
						DefaultEmptyState(
							title = R.string.bowler_list_empty_title,
							icon = R.drawable.bowler_list_empty_state,
							message = R.string.bowler_list_empty_message,
							action = R.string.bowler_list_add,
							onActionClick = onAddBowler,
						)
					}
				} else {
					header?.also {
						item {
							it()
						}
					}

					bowlersList(
						list = state.list,
						onBowlerClick = onBowlerClick,
					)
				}
			}
		}
	}
}

fun LazyListScope.bowlersList(
	list: List<BowlerListItem>,
	onBowlerClick: (UUID) -> Unit,
) {
	items(
		items = list,
		key = { it.id },
		contentType = { "bowler" },
	) { bowler ->
		BowlerItemRow(
			bowler = bowler,
			onClick = { onBowlerClick(bowler.id) },
		)
	}
}

sealed interface BowlersListUiState {
	data object Loading: BowlersListUiState
	data class Success(
		val list: List<BowlerListItem>,
	): BowlersListUiState
}