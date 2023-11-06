package ca.josephroque.bowlingcompanion.feature.bowlerslist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.R as RCoreDesign
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.util.UUID

@Composable
fun BowlerList(
	state: BowlersListUiState,
	onBowlerClick: (UUID) -> Unit,
	onArchiveBowler: (UUID?) -> Unit,
	onEditBowler: (UUID) -> Unit,
	onAddBowler: () -> Unit,
	modifier: Modifier = Modifier,
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	when (state) {
		BowlersListUiState.Loading -> Unit
		is BowlersListUiState.Success -> {
			state.bowlerToArchive?.let { bowler ->
				ArchiveDialog(
					itemName = bowler.name,
					onArchive = { onArchiveBowler(bowler.id) },
					onDismiss = { onArchiveBowler(null) },
				)
			}
		}
	}

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
						onArchiveBowler = onArchiveBowler,
						onEditBowler = onEditBowler,
					)
				}
			}
		}
	}
}

fun LazyListScope.bowlersList(
	list: List<BowlerListItem>,
	onBowlerClick: (UUID) -> Unit,
	onArchiveBowler: (UUID) -> Unit,
	onEditBowler: (UUID) -> Unit,
) {
	items(
		items = list,
		key = { it.id },
		contentType = { "bowler" },
	) { bowler ->
		val archiveAction = SwipeAction(
			icon = painterResource(RCoreDesign.drawable.ic_archive),
			background = colorResource(RCoreDesign.color.destructive),
			onSwipe = { onArchiveBowler(bowler.id) },
		)

		val editAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Default.Edit),
			background = colorResource(RCoreDesign.color.blue_300),
			onSwipe = { onEditBowler(bowler.id) },
		)

		SwipeableActionsBox(
			startActions = listOf(archiveAction),
			endActions = listOf(editAction),
		) {
			BowlerItemRow(
				bowler = bowler,
				onClick = { onBowlerClick(bowler.id) },
			)
		}
	}
}

sealed interface BowlersListUiState {
	data object Loading: BowlersListUiState
	data class Success(
		val bowlerToArchive: BowlerListItem?,
		val list: List<BowlerListItem>,
	): BowlersListUiState
}

@Preview
@Composable
fun BowlersListPreview() {
	Surface {
		BowlerList(
			state = BowlersListUiState.Success(
				bowlerToArchive = null,
				list = listOf(
					BowlerListItem(
						id = UUID.randomUUID(),
						name = "Joseph Roque",
						average = 200.0,
					),
					BowlerListItem(
						id = UUID.randomUUID(),
						name = "John Doe",
						average = 150.0,
					),
				),
			),
			onAddBowler = {},
			onBowlerClick = {},
			onArchiveBowler = {},
			onEditBowler = {},
		)
	}
}