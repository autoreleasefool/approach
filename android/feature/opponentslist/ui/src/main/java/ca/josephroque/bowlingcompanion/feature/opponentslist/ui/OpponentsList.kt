package ca.josephroque.bowlingcompanion.feature.opponentslist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.designsystem.components.ArchiveDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.list.footer
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.feature.bowlerslist.ui.bowlersList

@Composable
fun OpponentsList(
	state: OpponentsListUiState,
	onAction: (OpponentsListUiAction) -> Unit,
	modifier: Modifier = Modifier,
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
					icon = R.drawable.opponent_list_empty_state,
					message = R.string.opponent_list_empty_message,
					action = R.string.opponent_list_add,
					onActionClick = { onAction(OpponentsListUiAction.AddOpponentClicked) },
				)
			}
		} else {
			footer(R.string.opponent_list_description)

			bowlersList(
				list = state.list,
				onBowlerClick = { onAction(OpponentsListUiAction.OpponentClicked(it.id)) },
				onEditBowler = { onAction(OpponentsListUiAction.OpponentEdited(it.id)) },
				onArchiveBowler = { onAction(OpponentsListUiAction.OpponentArchived(it)) },
			)
		}
	}
}