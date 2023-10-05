package ca.josephroque.bowlingcompanion.core.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.Instant
import java.util.UUID

class League {
	companion object {
		val NUMBER_OF_GAMES_RANGE = 1..40
	}
}

@Immutable
data class LeagueDetails(
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val numberOfGames: Int?,
	val additionalPinFall: Int?,
	val additionalGames: Int?,
	val excludeFromStatistics: ExcludeFromStatistics,
)

@Immutable
data class LeagueListItem(
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	val lastSeriesDate: Instant?,
	val average: Double?,
)

enum class LeagueRecurrence {
	REPEATING,
	ONCE,
}