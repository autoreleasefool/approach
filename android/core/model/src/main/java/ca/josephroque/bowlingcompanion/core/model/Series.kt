package ca.josephroque.bowlingcompanion.core.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.util.UUID

enum class SeriesSortOrder {
	OLDEST_TO_NEWEST,
	NEWEST_TO_OLDEST,
	HIGHEST_TO_LOWEST,
	LOWEST_TO_HIGHEST,
}

enum class SeriesItemSize {
	DEFAULT,
	COMPACT,
	;

	val next: SeriesItemSize
		get() = when (this) {
			DEFAULT -> COMPACT
			COMPACT -> DEFAULT
		}
}

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
	val alley: AlleyDetails?,
	val scores: List<Int>,
)

data class SeriesDetailsProperties(
	val leagueId: UUID,
	val alleyId: UUID?,
	val id: UUID,
	val date: LocalDate,
	val total: Int,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class SeriesCreate(
	val leagueId: UUID,
	val id: UUID,
	val date: LocalDate,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
	val alleyId: UUID?,
)

data class SeriesUpdate(
	val id: UUID,
	val date: LocalDate,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
	val alleyId: UUID?,
)

enum class SeriesPreBowl {
	REGULAR,
	PRE_BOWL,
}