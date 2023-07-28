package ca.josephroque.bowlingcompanion.core.model

import kotlinx.datetime.Instant
import java.util.UUID

data class Series(
	val id: UUID,
	val date: Instant,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
)

enum class SeriesPreBowl {
	REGULAR,
	PRE_BOWL,
}