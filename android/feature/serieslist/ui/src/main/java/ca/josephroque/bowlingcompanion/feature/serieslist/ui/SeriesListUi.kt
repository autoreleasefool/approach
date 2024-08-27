package ca.josephroque.bowlingcompanion.feature.serieslist.ui

import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.datetime.LocalDate

data class SeriesListChartItem(
	val id: SeriesID,
	val date: LocalDate,
	val appliedDate: LocalDate?,
	val preBowl: SeriesPreBowl,
	val total: Int,
	val numberOfGames: Int,
	val scoreRange: IntRange,
	val scores: ChartEntryModelProducer?,
)

data class SeriesListUiState(
	val preBowlSeries: List<SeriesListChartItem>,
	val regularSeries: List<SeriesListChartItem>,
	val itemSize: SeriesItemSize,
	val seriesToArchive: SeriesListChartItem?,
) {
	val isEmpty: Boolean
		get() = preBowlSeries.isEmpty() && regularSeries.isEmpty()
}

sealed interface SeriesListUiAction {
	data object AddSeriesClicked : SeriesListUiAction
	data object UsePreBowlClicked : SeriesListUiAction

	data class SeriesClicked(val id: SeriesID) : SeriesListUiAction
	data class EditSeriesClicked(val id: SeriesID) : SeriesListUiAction
	data class ArchiveSeriesClicked(val series: SeriesListChartItem) : SeriesListUiAction

	data object ConfirmArchiveClicked : SeriesListUiAction
	data object DismissArchiveClicked : SeriesListUiAction
}
