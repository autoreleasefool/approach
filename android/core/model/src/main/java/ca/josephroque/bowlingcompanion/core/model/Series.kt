package ca.josephroque.bowlingcompanion.core.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.util.UUID

data class SeriesSummary(
	val id: UUID,
	val date: LocalDate,
)

data class SeriesListItem(
	val properties: SeriesListProperties,
	val scores: List<Int>,
)

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

data class ArchivedSeries(
	val id: UUID,
	val date: LocalDate,
	val bowlerName: String,
	val leagueName: String,
	val numberOfGames: Int,
	val archivedOn: Instant,
)

data class SeriesDetails(
	val properties: SeriesDetailsProperties,
	val scores: List<Int>,
)

data class SeriesDetailsProperties(
	val id: UUID,
	val date: LocalDate,
	val total: Int,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
)

enum class SeriesPreBowl {
	REGULAR,
	PRE_BOWL,
}