package ca.josephroque.bowlingcompanion.feature.teamdetails.ui

import ca.josephroque.bowlingcompanion.core.model.TeamMemberListItem
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.util.UUID
import kotlinx.datetime.LocalDate

data class TeamSeriesListChartItem(
	val id: UUID,
	val date: LocalDate,
	val total: Int,
	val numberOfGames: Int,
	val scoreRange: IntRange,
	val chart: ChartEntryModelProducer,
	val members: List<TeamMemberSeriesListChartItem>,
)

data class TeamMemberSeriesListChartItem(
	val id: UUID,
	val name: String,
	val scoreRange: IntRange,
	val chart: ChartEntryModelProducer,
)

data class TeamDetailsUiState(
	val members: List<TeamMemberListItem>,
	val series: List<TeamSeriesListChartItem>,
)

sealed interface TeamDetailsUiAction {
//	data class MemberMoved(val from: Int, val to: Int) : TeamDetailsUiAction
}

data class TeamDetailsTopBarUiState(val teamName: String? = null)

sealed interface TeamDetailsTopBarUiAction {
	data object BackClicked : TeamDetailsTopBarUiAction
	data object AddSeriesClicked : TeamDetailsTopBarUiAction
}