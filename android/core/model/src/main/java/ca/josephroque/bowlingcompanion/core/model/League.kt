package ca.josephroque.bowlingcompanion.core.model

import kotlinx.datetime.LocalDate
import java.util.UUID

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

enum class LeagueRecurrence {
	REPEATING,
	ONCE,
}