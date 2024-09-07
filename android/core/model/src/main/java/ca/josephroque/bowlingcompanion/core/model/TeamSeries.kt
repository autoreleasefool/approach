package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class TeamSeriesID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): TeamSeriesID = TeamSeriesID(UUID.randomUUID())
		fun fromString(string: String): TeamSeriesID = TeamSeriesID(UUID.fromString(string))
	}
}

enum class TeamSeriesSortOrder {
	NEWEST_TO_OLDEST,
	OLDEST_TO_NEWEST,
	HIGHEST_TO_LOWEST,
	LOWEST_TO_HIGHEST,
}

data class TeamSeriesSummary(val id: TeamSeriesID, val date: LocalDate, val total: Int)

data class TeamSeriesDetails(
	val id: TeamSeriesID,
	val date: LocalDate,
	val total: Int,
	val scores: List<Int>,
	val members: List<TeamSeriesMemberDetails>,
)

data class TeamSeriesMemberDetails(val id: BowlerID, val name: String, val scores: List<Int>)

data class TeamSeriesConnect(
	val id: TeamSeriesID,
	val teamId: TeamID,
	val seriesIds: List<SeriesID>,
	val date: LocalDate,
)

data class TeamSeriesCreate(
	val teamId: TeamID,
	val id: TeamSeriesID,
	val leagues: List<LeagueID>,
	val date: LocalDate,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val manualScores: Map<LeagueID, List<Int>>?,
	val excludeFromStatistics: ExcludeFromStatistics,
	val alleyId: AlleyID?,
)

data class TeamSeriesUpdate(val id: TeamSeriesID, val seriesIds: List<SeriesID>)
