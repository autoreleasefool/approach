package ca.josephroque.bowlingcompanion.core.model

import kotlinx.datetime.LocalDate
import java.util.UUID

data class SeriesListProperties(
	val id: UUID,
	val date: LocalDate,
	val total: Int,
	val preBowl: SeriesPreBowl,
)

data class TrackableSeries(
	val id: UUID,
	val numberOfGames: Int,
	val total: Int,
	val date: LocalDate,
)

enum class SeriesPreBowl {
	REGULAR,
	PRE_BOWL,
}