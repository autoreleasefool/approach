package ca.josephroque.bowlingcompanion.feature.serieslist

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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