package ca.josephroque.bowlingcompanion.feature.bowlerslist.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.EmptyStateAction
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerListItem
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun BowlersList(
	state: BowlersListUiState,
	onAction: (BowlersListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	state.bowlerToArchive?.let {
		ArchiveDialog(
			itemName = it.name,
			onArchive = { onAction(BowlersListUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(BowlersListUiAction.DismissArchiveClicked) },
		)
	}

	LazyColumn(
		modifier = modifier,
		contentPadding = contentPadding,
	) {
		if (state.list.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.bowler_list_empty_title,
					icon = R.drawable.bowler_list_empty_state,
					message = R.string.bowler_list_empty_message,
					action = EmptyStateAction(
						title = R.string.bowler_list_add,
						onClick = { onAction(BowlersListUiAction.AddBowlerClicked) },
					),
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
				onBowlerClick = { onAction(BowlersListUiAction.BowlerClicked(it)) },
				onArchiveBowler = { onAction(BowlersListUiAction.BowlerArchived(it)) },
				onEditBowler = { onAction(BowlersListUiAction.BowlerEdited(it)) },
			)
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
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
			icon = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_archive),
			background = colorResource(
				ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
			),
			onSwipe = { onArchiveBowler(it) },
		)

		val editAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Default.Edit),
			background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_300),
			onSwipe = { onEditBowler(it) },
		)

		SwipeableActionsBox(
			startActions = listOf(archiveAction),
			endActions = listOf(editAction),
			modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
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
private fun BowlersListPreview() {
	Surface {
		BowlersList(
			state = BowlersListUiState(
				bowlerToArchive = null,
				list = listOf(
					BowlerListItem(
						id = BowlerID.randomID(),
						name = "Joseph Roque",
						average = 200.0,
					),
					BowlerListItem(
						id = BowlerID.randomID(),
						name = "John Doe",
						average = 150.0,
					),
				),
			),
			onAction = {},
		)
	}
}
