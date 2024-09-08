package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.SeriesItemSize
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSortOrder
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import kotlinx.datetime.LocalDate

sealed interface TeamSeriesListItem {
	val id: TeamSeriesID
	val date: LocalDate
	val total: Int

	data class Chart(val item: TeamSeriesListChartItem) : TeamSeriesListItem {
		override val id: TeamSeriesID
			get() = item.id
		override val date: LocalDate
			get() = item.date
		override val total: Int
			get() = item.total
	}
	data class Summary(val item: TeamSeriesSummary) : TeamSeriesListItem {
		override val id: TeamSeriesID
			get() = item.id
		override val date: LocalDate
			get() = item.date
		override val total: Int
			get() = item.total
	}
}

data class TeamSeriesListChartItem(
	val id: TeamSeriesID,
	val date: LocalDate,
	val total: Int,
	val numberOfGames: Int,
	val scoreRange: IntRange,
	val chart: ChartEntryModelProducer,
	val members: List<TeamMemberSeriesListChartItem>,
)

data class TeamMemberSeriesListChartItem(
	val id: BowlerID,
	val name: String,
	val scoreRange: IntRange,
	val chart: ChartEntryModelProducer,
)

data class TeamDetailsUiState(
	val members: List<TeamMemberListItem>,
	val series: List<TeamSeriesListItem>,
	val seriesItemSize: SeriesItemSize,
	val seriesToArchive: ArchiveSeriesUiState,
)

data class ArchiveSeriesUiState(
	val seriesToArchive: TeamSeriesListItem?,
	val isArchiveMemberSeriesVisible: Boolean,
)

sealed interface TeamDetailsUiAction {
	data object AddSeriesClicked : TeamDetailsUiAction

	data class SeriesAppeared(val id: TeamSeriesID) : TeamDetailsUiAction
	data class SeriesClicked(val series: TeamSeriesListItem) : TeamDetailsUiAction
	data class EditSeriesClicked(val series: TeamSeriesListItem) : TeamDetailsUiAction
	data class ArchiveSeriesClicked(val series: TeamSeriesListItem) : TeamDetailsUiAction

	data object ConfirmArchiveClicked : TeamDetailsUiAction
	data object DismissArchiveClicked : TeamDetailsUiAction

	data object ArchiveMemberSeriesClicked : TeamDetailsUiAction
	data object KeepMemberSeriesClicked : TeamDetailsUiAction
	data object DismissArchiveMemberSeriesClicked : TeamDetailsUiAction
}

data class TeamDetailsTopBarUiState(
	val teamName: String? = null,
	val isSortOrderMenuVisible: Boolean = false,
	val isSortOrderMenuExpanded: Boolean = false,
	val sortOrder: TeamSeriesSortOrder = TeamSeriesSortOrder.NEWEST_TO_OLDEST,
	val isSeriesItemSizeVisible: Boolean = false,
	val seriesItemSize: SeriesItemSize = SeriesItemSize.DEFAULT,
)

sealed interface TeamDetailsTopBarUiAction {
	data object BackClicked : TeamDetailsTopBarUiAction
	data object AddSeriesClicked : TeamDetailsTopBarUiAction

	data class SeriesItemSizeToggled(val itemSize: SeriesItemSize) : TeamDetailsTopBarUiAction
	data object SortClicked : TeamDetailsTopBarUiAction
	data object SortDismissed : TeamDetailsTopBarUiAction
	data class SortOrderClicked(val sortOrder: TeamSeriesSortOrder) : TeamDetailsTopBarUiAction
}

sealed interface TeamDetailsFloatingActionButtonUiAction {
	data object AddSeriesClicked : TeamDetailsFloatingActionButtonUiAction
}
