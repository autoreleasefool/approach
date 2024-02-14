package ca.josephroque.bowlingcompanion.feature.resourcepicker.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState

@Composable
fun ResourcePicker(
	state: ResourcePickerUiState,
	onAction: (ResourcePickerUiAction) -> Unit,
	itemContent: @Composable BoxScope.(item: ResourceItem) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(modifier = modifier) {
		if (state.items.isEmpty()) {
			item {
				DefaultEmptyState(
					title = R.string.resource_picker_empty_title,
					icon = R.drawable.resource_picker_empty_state,
					message = R.string.resource_picker_empty_message,
					action = R.string.resource_picker_empty_action,
					onActionClick = { onAction(ResourcePickerUiAction.BackClicked) },
				)
			}
		} else {
			items(
				items = state.items,
				key = { it.id },
			) { item ->
				PickableRow(
					isSelected = state.selectedItems.contains(item.id),
					onClick = { onAction(ResourcePickerUiAction.ItemClicked(item.id)) },
					content = {
						itemContent(item)
					},
				)
			}
		}
	}
}
