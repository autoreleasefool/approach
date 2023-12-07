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
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.util.UUID

@Composable
fun BowlersList(
	state: BowlersListUiState,
	onAction: (BowlersListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	state.bowlerToArchive?.let {
		ArchiveDialog(
			itemName = it.name,
			onArchive = { onAction(BowlersListUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(BowlersListUiAction.DismissArchiveClicked) },
		)
	}

	LazyColumn(modifier = modifier) {
		if (state.list.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.bowler_list_empty_title,
					icon = R.drawable.bowler_list_empty_state,
					message = R.string.bowler_list_empty_message,
					action = R.string.bowler_list_add,
					onActionClick = { onAction(BowlersListUiAction.AddBowlerClicked) },
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
				onBowlerClick = { onAction(BowlersListUiAction.BowlerClicked(it.id)) },
				onArchiveBowler = { onAction(BowlersListUiAction.BowlerArchived(it)) },
				onEditBowler = { onAction(BowlersListUiAction.BowlerEdited(it.id)) },
			)
		}
	}
}

fun LazyListScope.bowlersList(
	list: List<BowlerListItem>,
	onBowlerClick: (BowlerListItem) -> Unit,
	onArchiveBowler: (BowlerListItem) -> Unit,
	onEditBowler: (BowlerListItem) -> Unit,
) {
	items(
		items = list,
		key = { it.id },
	) {
		val archiveAction = SwipeAction(
			icon = painterResource(RCoreDesign.drawable.ic_archive),
			background = colorResource(RCoreDesign.color.destructive),
			onSwipe = { onArchiveBowler(it) },
		)

		val editAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Default.Edit),
			background = colorResource(RCoreDesign.color.blue_300),
			onSwipe = { onEditBowler(it) },
		)

		SwipeableActionsBox(
			startActions = listOf(archiveAction),
			endActions = listOf(editAction),
		) {
			BowlerRow(
				name = it.name,
				average = it.average,
				onClick = { onBowlerClick(it) },
			)
		}
	}
}

@Preview
@Composable
fun BowlersListPreview() {
	Surface {
		BowlersList(
			state = BowlersListUiState(
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
			onAction = {},
		)
	}
}