package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize

object League {
	val NumberOfGamesRange = 1..40
}

enum class LeagueSortOrder {
	MOST_RECENTLY_USED,
	ALPHABETICAL,
}

@JvmInline
@Parcelize
value class LeagueID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): LeagueID = LeagueID(UUID.randomUUID())
		fun fromString(string: String): LeagueID = LeagueID(UUID.fromString(string))
	}
}

data class LeagueSummary(val id: LeagueID, val name: String)

data class LeagueDetails(
	val id: LeagueID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val numberOfGames: Int?,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class LeagueListItem(
	val id: LeagueID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val lastSeriesDate: LocalDate?,
	val average: Double?,
)

data class ArchivedLeague(
	val id: LeagueID,
	val name: String,
	val bowlerName: String,
	val numberOfSeries: Int,
	val numberOfGames: Int,
	val archivedOn: Instant,
)

data class LeagueCreate(
	val bowlerId: BowlerID,
	val id: LeagueID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val numberOfGames: Int?,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class LeagueUpdate(
	val id: LeagueID,
	val name: String,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

enum class LeagueRecurrence {
	REPEATING,
	ONCE,
}
