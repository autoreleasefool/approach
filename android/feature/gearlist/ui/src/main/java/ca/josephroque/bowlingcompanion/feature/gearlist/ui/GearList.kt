package ca.josephroque.bowlingcompanion.feature.gearlist.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.DefaultEmptyState
import ca.josephroque.bowlingcompanion.core.designsystem.components.state.LoadingState
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import java.util.UUID

@Composable
fun GearList(
	state: GearListUiState,
	onAddGear: () -> Unit,
	onGearClick: (UUID) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyColumn(modifier = modifier) {
		when (state) {
			GearListUiState.Loading -> {
				item {
					LoadingState()
				}
			}
			is GearListUiState.Success -> {
				if (state.list.isEmpty()) {
					item {
						DefaultEmptyState(
							title = R.string.gear_list_empty_title,
							icon = R.drawable.gear_list_empty_state,
							message = R.string.gear_list_empty_message,
							action = R.string.gear_list_add,
							onActionClick = onAddGear
						)
					}
				} else {
					gearList(
						list = state.list,
						onGearClick = onGearClick,
					)
				}
			}
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

sealed interface GearListUiState {
	data object Loading: GearListUiState
	data class Success(
		val list: List<GearListItem>,
	): GearListUiState
}