package ca.josephroque.bowlingcompanion.feature.alleyslist.ui

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import ca.josephroque.bowlingcompanion.core.model.AlleyListItem
import java.util.UUID

fun LazyListScope.alleysList(
	alleysListState: AlleysListUiState,
	onAlleyClick: (UUID) -> Unit,
) {
	when (alleysListState) {
		AlleysListUiState.Loading -> Unit
		is AlleysListUiState.Success -> {
			items(
				items = alleysListState.list,
				key = { it.id },
			) { alley ->
				AlleyItemRow(
					alley = alley,
					onClick = { onAlleyClick(alley.id) },
				)
			}
		}
	}
}

sealed interface AlleysListUiState {
	data object Loading: AlleysListUiState
	data class Success(
		val list: List<AlleyListItem>,
	): AlleysListUiState
}