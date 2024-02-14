package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

object League {
	val NumberOfGamesRange = 1..40
}

data class LeagueSummary(
	val id: UUID,
	val name: String,
)

data class LeagueDetails(
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val numberOfGames: Int?,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class LeagueListItem(
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val lastSeriesDate: LocalDate?,
	val average: Double?,
)

data class ArchivedLeague(
	val id: UUID,
	val name: String,
	val bowlerName: String,
	val numberOfSeries: Int,
	val numberOfGames: Int,
	val archivedOn: Instant,
)

data class LeagueCreate(
	val bowlerId: UUID,
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val numberOfGames: Int?,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class LeagueUpdate(
	val id: UUID,
	val name: String,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

enum class LeagueRecurrence {
	REPEATING,
	ONCE,
}
