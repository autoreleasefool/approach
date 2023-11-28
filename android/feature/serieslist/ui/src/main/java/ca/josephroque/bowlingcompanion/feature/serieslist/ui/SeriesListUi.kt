package ca.josephroque.bowlingcompanion.feature.serieslist.ui

import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import kotlinx.datetime.LocalDate
import java.util.UUID

data class SeriesListChartItem(
	val id: UUID,
	val date: LocalDate,
	val preBowl: SeriesPreBowl,
	val total: Int,
	val numberOfGames: Int,
	val scores: ChartEntryModel?,
)

data class SeriesListUiState(
	val list: List<SeriesListChartItem>,
	val seriesToArchive: SeriesListChartItem?,
)

sealed interface SeriesListUiAction {
	data object AddSeriesClicked: SeriesListUiAction

	data class SeriesClicked(val id: UUID): SeriesListUiAction
	data class EditSeriesClicked(val id: UUID): SeriesListUiAction
	data class ArchiveSeriesClicked(val series: SeriesListChartItem): SeriesListUiAction

	data object ConfirmArchiveClicked: SeriesListUiAction
	data object DismissArchiveClicked: SeriesListUiAction
}