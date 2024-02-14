package ca.josephroque.bowlingcompanion.feature.alleyslist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.colorResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.DeleteDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import ca.josephroque.bowlingcompanion.core.model.ui.AlleyRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun AlleysList(
	state: AlleysListUiState,
	onAction: (AlleysListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	state.alleyToDelete?.let {
		DeleteDialog(
			itemName = it.name,
			onDelete = { onAction(AlleysListUiAction.ConfirmDeleteClicked) },
			onDismiss = { onAction(AlleysListUiAction.DismissDeleteClicked) },
		)
	}

	LazyColumn(modifier = modifier) {
		if (state.list.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.alleys_list_empty_title,
					icon = R.drawable.alleys_list_empty_state,
					message = R.string.alleys_list_empty_message,
					action = R.string.alley_list_add,
					onActionClick = { onAction(AlleysListUiAction.AddAlleyClicked) },
				)
			}
		} else {
			alleysList(
				list = state.list,
				onAlleyClick = { onAction(AlleysListUiAction.AlleyClicked(it.id)) },
				onAlleyDelete = { onAction(AlleysListUiAction.AlleyDeleted(it)) },
				onAlleyEdit = { onAction(AlleysListUiAction.AlleyEdited(it.id)) },
			)
		}
	}
}

fun LazyListScope.alleysList(
	list: List<AlleyListItem>,
	onAlleyClick: (AlleyListItem) -> Unit,
	onAlleyDelete: ((AlleyListItem) -> Unit)? = null,
	onAlleyEdit: ((AlleyListItem) -> Unit)? = null,
) {
	items(
		items = list,
		key = { it.id },
	) { alley ->
		val deleteAction = onAlleyDelete?.let {
			SwipeAction(
				icon = rememberVectorPainter(Icons.Filled.Delete),
				background = colorResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
				),
				onSwipe = { it(alley) },
			)
		}

		val editAction = onAlleyEdit?.let {
			SwipeAction(
				icon = rememberVectorPainter(Icons.Default.Edit),
				background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_300),
				onSwipe = { it(alley) },
			)
		}

		SwipeableActionsBox(
			startActions = listOfNotNull(deleteAction),
			endActions = listOfNotNull(editAction),
		) {
			AlleyRow(
				name = alley.name,
				material = alley.material,
				mechanism = alley.mechanism,
				pinBase = alley.pinBase,
				pinFall = alley.pinFall,
				onClick = { onAlleyClick(alley) },
			)
		}
	}
}
