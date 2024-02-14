package ca.josephroque.bowlingcompanion.feature.gearlist.ui

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
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.ui.GearRow
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun GearList(
	state: GearListUiState,
	onAction: (GearListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	state.gearToDelete?.let {
		DeleteDialog(
			itemName = it.name,
			onDelete = { onAction(GearListUiAction.ConfirmDeleteClicked) },
			onDismiss = { onAction(GearListUiAction.DismissDeleteClicked) },
		)
	}

	LazyColumn(modifier = modifier) {
		if (state.list.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.gear_list_empty_title,
					icon = R.drawable.gear_list_empty_state,
					message = R.string.gear_list_empty_message,
					action = R.string.gear_list_add,
					onActionClick = { onAction(GearListUiAction.AddGearClicked) },
				)
			}
		} else {
			gearList(
				list = state.list,
				onGearClick = { onAction(GearListUiAction.GearClicked(it.id)) },
				onGearDelete = { onAction(GearListUiAction.GearDeleted(it)) },
				onGearEdit = { onAction(GearListUiAction.GearEdited(it.id)) },
			)
		}
	}
}

fun LazyListScope.gearList(
	list: List<GearListItem>,
	onGearClick: (GearListItem) -> Unit,
	onGearDelete: ((GearListItem) -> Unit)? = null,
	onGearEdit: ((GearListItem) -> Unit)? = null,
) {
	items(
		items = list,
		key = { it.id },
	) { gear ->
		val deleteAction = onGearDelete?.let {
			SwipeAction(
				icon = rememberVectorPainter(Icons.Filled.Delete),
				background = colorResource(
					ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive,
				),
				onSwipe = { it(gear) },
			)
		}

		val editAction = onGearEdit?.let {
			SwipeAction(
				icon = rememberVectorPainter(Icons.Default.Edit),
				background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_300),
				onSwipe = { it(gear) },
			)
		}

		SwipeableActionsBox(
			startActions = listOfNotNull(deleteAction),
			endActions = listOfNotNull(editAction),
		) {
			GearRow(
				name = gear.name,
				ownerName = gear.ownerName,
				kind = gear.kind,
				avatar = gear.avatar,
				onClick = { onGearClick(gear) },
			)
		}
	}
}
