package ca.josephroque.bowlingcompanion.core.model

import androidx.room.ColumnInfo
import kotlinx.datetime.Instant
import java.util.UUID

data class LeagueCreate(
	@ColumnInfo(name = "bowler_id") val bowlerId: UUID,
	val id: UUID,
	val name: String,
	val recurrence: LeagueRecurrence,
	@ColumnInfo(name = "number_of_games") val numberOfGames: Int?,
	@ColumnInfo(name = "additional_pin_fall")val additionalPinFall: Int?,
	@ColumnInfo(name = "additional_games") val additionalGames: Int?,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

data class LeagueUpdate(
	val id: UUID,
	val name: String,
	@ColumnInfo(name = "additional_pin_fall") val additionalPinFall: Int?,
	@ColumnInfo(name = "additional_games") val additionalGames: Int?,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

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