package ca.josephroque.bowlingcompanion.core.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import kotlinx.datetime.Instant
import java.util.UUID

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