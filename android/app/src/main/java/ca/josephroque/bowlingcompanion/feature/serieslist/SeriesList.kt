package ca.josephroque.bowlingcompanion.feature.serieslist

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ca.josephroque.bowlingcompanion.R
import java.util.UUID

fun LazyListScope.seriesList(
	seriesListState: SeriesListUiState,
	onSeriesClick: (UUID) -> Unit,
) {
	item {
		Text(
			text = stringResource(R.string.series_list_title),
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
		)
	}

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