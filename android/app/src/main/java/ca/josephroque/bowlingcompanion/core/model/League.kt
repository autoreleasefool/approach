package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class League(
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val numberOfGames: Int?,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

enum class LeagueRecurrence {
	REPEATING,
	ONCE,
}