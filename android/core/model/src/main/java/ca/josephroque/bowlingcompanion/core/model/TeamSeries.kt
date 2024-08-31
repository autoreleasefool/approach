package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID
import kotlinx.datetime.LocalDate

data class TeamSeriesSummary(val id: UUID, val date: LocalDate)

data class TeamSeriesDetails(val summary: TeamSeriesSummary, val scores: List<Int>)

data class TeamMemberSeriesSummary(val id: UUID, val teamMemberName: String)

data class TeamMemberSeriesDetails(val summary: TeamMemberSeriesSummary, val scores: List<Int>)

data class TeamSeriesListItem(
	val teamSeries: TeamSeriesDetails,
	val memberSeries: List<TeamMemberSeriesDetails>,
)

data class TeamSeriesConnect(
	val id: UUID,
	val teamId: UUID,
	val seriesIds: List<UUID>,
	val date: LocalDate,
)

data class TeamSeriesCreate(
	val teamId: UUID,
	val id: UUID,
	val leagues: List<UUID>,
	val date: LocalDate,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val manualScores: Map<UUID, List<Int>>?,
	val excludeFromStatistics: ExcludeFromStatistics,
	val alleyId: UUID?,
)
