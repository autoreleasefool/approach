package ca.josephroque.bowlingcompanion.feature.serieslist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.UUID

fun LazyListScope.seriesList(
	seriesListState: SeriesListUiState,
	onSeriesClick: (UUID) -> Unit,
) {
	when (seriesListState) {
		SeriesListUiState.Loading -> Unit
		is SeriesListUiState.Success -> {
			items(
				items = seriesListState.list,
				key = { it.id },
				contentType = { "series" },
			) { series ->
				SeriesItemRow(
					series = series,
					onClick = { onSeriesClick(series.id) },
					modifier = Modifier.padding(bottom = 16.dp),
				)
			}
		}
	}
}

sealed interface SeriesListUiState {
	data object Loading: SeriesListUiState
	data class Success(
		val list: List<SeriesChartable>,
	): SeriesListUiState
}