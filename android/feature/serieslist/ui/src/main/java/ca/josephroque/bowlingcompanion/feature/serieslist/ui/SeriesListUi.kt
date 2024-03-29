package ca.josephroque.bowlingcompanion.feature.serieslist.ui

import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.util.UUID
import kotlinx.datetime.LocalDate

data class SeriesListChartItem(
	val id: UUID,
	val date: LocalDate,
	val appliedDate: LocalDate?,
	val preBowl: SeriesPreBowl,
	val total: Int,
	val numberOfGames: Int,
	val lowestScore: Int,
	val highestScore: Int,
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

	data class SeriesClicked(val id: UUID) : SeriesListUiAction
	data class EditSeriesClicked(val id: UUID) : SeriesListUiAction
	data class ArchiveSeriesClicked(val series: SeriesListChartItem) : SeriesListUiAction

	data object ConfirmArchiveClicked : SeriesListUiAction
	data object DismissArchiveClicked : SeriesListUiAction
}
