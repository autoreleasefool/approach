package ca.josephroque.bowlingcompanion.feature.serieslist.ui

import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.util.UUID
import kotlinx.datetime.LocalDate

data class SeriesListChartItem(
	val id: UUID,
	val date: LocalDate,
	val preBowl: SeriesPreBowl,
	val total: Int,
	val numberOfGames: Int,
	val lowestScore: Int,
	val highestScore: Int,
	val scores: ChartEntryModelProducer?,
)

data class SeriesListUiState(
	val list: List<SeriesListChartItem>,
	val itemSize: SeriesItemSize,
	val seriesToArchive: SeriesListChartItem?,
)

sealed interface SeriesListUiAction {
	data object AddSeriesClicked : SeriesListUiAction

	data class SeriesClicked(val id: UUID) : SeriesListUiAction
	data class EditSeriesClicked(val id: UUID) : SeriesListUiAction
	data class ArchiveSeriesClicked(val series: SeriesListChartItem) : SeriesListUiAction

	data object ConfirmArchiveClicked : SeriesListUiAction
	data object DismissArchiveClicked : SeriesListUiAction
}
