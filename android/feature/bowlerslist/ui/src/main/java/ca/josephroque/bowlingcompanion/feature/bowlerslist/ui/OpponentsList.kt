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
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.OpponentListItem
import ca.josephroque.bowlingcompanion.core.model.ui.BowlerRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.util.UUID

@Composable
fun OpponentsList(
	state: OpponentsListUiState,
	onAction: (OpponentsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
	header: (@Composable LazyItemScope.() -> Unit)? = null,
) {
	state.opponentToArchive?.let {
		ArchiveDialog(
			itemName = it.name,
			onArchive = { onAction(OpponentsListUiAction.ConfirmArchiveClicked) },
			onDismiss = { onAction(OpponentsListUiAction.DismissArchiveClicked) },
		)
	}

	LazyColumn(modifier = modifier) {
		if (state.list.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.opponent_list_empty_title,
					icon = R.drawable.bowler_list_empty_state,
					message = R.string.opponent_list_empty_message,
					action = R.string.opponent_list_add,
					onActionClick = { onAction(OpponentsListUiAction.AddOpponentClicked) },
				)
			}
		} else {
			header?.also {
				item {
					it()
				}
			}

			opponentsList(
				list = state.list,
				onOpponentClick = { onAction(OpponentsListUiAction.OpponentClicked(it)) },
				onArchiveOpponent = { onAction(OpponentsListUiAction.OpponentArchived(it)) },
				onEditOpponent = { onAction(OpponentsListUiAction.OpponentEdited(it)) },
			)
		}
	}
}

fun LazyListScope.opponentsList(
	list: List<OpponentListItem>,
	onOpponentClick: (OpponentListItem) -> Unit,
	onArchiveOpponent: (OpponentListItem) -> Unit,
	onEditOpponent: (OpponentListItem) -> Unit,
) {
	items(
		items = list,
		key = { it.id },
	) {
		val archiveAction = SwipeAction(
			icon = painterResource(ca.josephroque.bowlingcompanion.core.designsystem.R.drawable.ic_archive),
			background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive),
			onSwipe = { onArchiveOpponent(it) },
		)

		val editAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Default.Edit),
			background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_300),
			onSwipe = { onEditOpponent(it) },
		)

		SwipeableActionsBox(
			startActions = listOf(archiveAction),
			endActions = listOf(editAction),
		) {
			BowlerRow(
				name = it.name,
				kind = it.kind,
				onClick = { onOpponentClick(it) },
			)
		}
	}
}

@Preview
@Composable
private fun OpponentsListPreview() {
	Surface {
		OpponentsList(
			state = OpponentsListUiState(
				opponentToArchive = null,
				list = listOf(
					OpponentListItem(
						id = UUID.randomUUID(),
						name = "Joseph Roque",
						kind = BowlerKind.OPPONENT,
					),
					OpponentListItem(
						id = UUID.randomUUID(),
						name = "John Doe",
						kind = BowlerKind.PLAYABLE,
					),
				),
			),
			onAction = {},
		)
	}
}