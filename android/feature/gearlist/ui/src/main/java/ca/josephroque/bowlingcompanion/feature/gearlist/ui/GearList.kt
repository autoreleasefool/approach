package ca.josephroque.bowlingcompanion.feature.gearlist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.designsystem.components.DeleteDialog
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.model.GearListItem
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
				onGearClick = { onAction(GearListUiAction.GearClicked(it)) },
			)
		}
	}
}

fun LazyListScope.gearList(
	list: List<GearListItem>,
	onGearClick: (UUID) -> Unit,
) {
	items(
		items = list,
		key = { it.id },
		contentType = { "gear" },
	) { gear ->
		GearItemRow(
			gear = gear,
			onClick = { onGearClick(gear.id) },
		)
	}
}