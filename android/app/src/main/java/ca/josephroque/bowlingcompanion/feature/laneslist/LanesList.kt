package ca.josephroque.bowlingcompanion.feature.laneslist

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import ca.josephroque.bowlingcompanion.core.model.LaneListItem

fun LazyListScope.lanesList(
	lanesListState: LanesListUiState,
) {
	when (lanesListState) {
		LanesListUiState.Loading -> Unit
		is LanesListUiState.Success ->
			items(
				items = lanesListState.list,
				key = { it.id },
				contentType = { "lane" },
			) { lane ->
				LaneItemRow(lane = lane)
			}
	}
}

sealed interface LanesListUiState {
	data object Loading: LanesListUiState
	data class Success(
		val list: List<LaneListItem>,
	): LanesListUiState
}