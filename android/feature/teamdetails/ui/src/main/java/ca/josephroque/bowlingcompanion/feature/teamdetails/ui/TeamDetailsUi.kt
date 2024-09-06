package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesSummary
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.util.UUID
import kotlinx.datetime.LocalDate

sealed interface TeamSeriesListItem {
	val id: UUID
	val date: LocalDate
	val total: Int

	data class Chart(val item: TeamSeriesListChartItem) : TeamSeriesListItem {
		override val id: UUID
			get() = item.id.value
		override val date: LocalDate
			get() = item.date
		override val total: Int
			get() = item.total
	}
	data class Summary(val item: TeamSeriesSummary) : TeamSeriesListItem {
		override val id: UUID
			get() = item.id.value
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
)

sealed interface TeamDetailsUiAction {
	data object AddSeriesClicked : TeamDetailsUiAction
}

data class TeamDetailsTopBarUiState(val teamName: String? = null)

sealed interface TeamDetailsTopBarUiAction {
	data object BackClicked : TeamDetailsTopBarUiAction
}

sealed interface TeamDetailsFloatingActionButtonUiAction {
	data object AddSeriesClicked : TeamDetailsFloatingActionButtonUiAction
}
