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
import androidx.compose.ui.res.painterResource
import ca.josephroque.bowlingcompanion.core.designsystem.components.DeleteDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.util.UUID

@Composable
fun GearList(
	state: GearListUiState,
	onAction: (GearListUiAction) -> Unit,
	modifier: Modifier = Modifier,
) {
	if (state.gearToDelete != null) {
		DeleteDialog(
			itemName = state.gearToDelete.name,
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
				isDeleteEnabled = true,
				isEditEnabled = true,
			)
		}
	}
}

fun LazyListScope.gearList(
	list: List<GearListItem>,
	onGearClick: (GearListItem) -> Unit,
	onGearDelete: (GearListItem) -> Unit = {},
	onGearEdit: (GearListItem) -> Unit = {},
	isDeleteEnabled: Boolean = false,
	isEditEnabled: Boolean = false,
) {
	items(
		items = list,
		key = { it.id },
		contentType = { "gear" },
	) { gear ->
		val deleteAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Filled.Delete),
			background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.destructive),
			onSwipe = { onGearDelete(gear) },
		)

		val editAction = SwipeAction(
			icon = rememberVectorPainter(Icons.Default.Edit),
			background = colorResource(ca.josephroque.bowlingcompanion.core.designsystem.R.color.blue_300),
			onSwipe = { onGearEdit(gear) },
		)

		SwipeableActionsBox(
			startActions = if (isDeleteEnabled) listOf(deleteAction) else emptyList(),
			endActions = if (isEditEnabled) listOf(editAction) else emptyList(),
		) {
			GearItemRow(
				gear = gear,
				onClick = { onGearClick(gear) },
			)
		}
	}
}