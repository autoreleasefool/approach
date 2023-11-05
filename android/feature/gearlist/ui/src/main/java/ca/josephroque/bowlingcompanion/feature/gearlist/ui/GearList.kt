package ca.josephroque.bowlingcompanion.feature.gearlist.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import java.util.UUID

fun LazyListScope.gearList(
	gearListState: GearListUiState,
	onGearClick: (UUID) -> Unit,
) {
	when (gearListState) {
		GearListUiState.Loading -> Unit
		is GearListUiState.Success -> {
			items(
				items = gearListState.list,
				key = { it.id },
				contentType = { "gear" },
			) { gear ->
				GearItemRow(
					gear = gear,
					onClick = { onGearClick(gear.id) },
				)
			}
		}
	}
}

sealed interface GearListUiState {
	data object Loading: GearListUiState
	data class Success(
		val list: List<GearListItem>,
	): GearListUiState
}