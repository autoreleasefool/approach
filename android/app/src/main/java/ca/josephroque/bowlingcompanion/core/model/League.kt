package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

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

data class LeagueListItem(
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val average: Double?,
)

enum class LeagueRecurrence {
	REPEATING,
	ONCE,
}