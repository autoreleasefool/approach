package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize

object Series {
	const val DEFAULT_NUMBER_OF_GAMES = 4
}

@JvmInline
@Parcelize
value class SeriesID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): SeriesID = SeriesID(UUID.randomUUID())
		fun fromString(string: String): SeriesID = SeriesID(UUID.fromString(string))
	}
}

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

data class SeriesSummary(val id: SeriesID, val date: LocalDate)

data class SeriesListItem(val properties: SeriesListProperties, val scores: List<Int>)

data class SeriesListProperties(
	val id: SeriesID,
	val date: LocalDate,
	val total: Int,
	val preBowl: SeriesPreBowl,
	val appliedDate: LocalDate?,
)

data class TrackableSeries(
	val id: SeriesID,
	val numberOfGames: Int,
	val total: Int,
	val date: LocalDate,
)

data class ArchivedSeries(
	val id: SeriesID,
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
	val leagueId: LeagueID,
	val alleyId: UUID?,
	val id: SeriesID,
	val date: LocalDate,
	val appliedDate: LocalDate?,
	val total: Int,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class SeriesCreate(
	val leagueId: LeagueID,
	val id: SeriesID,
	val date: LocalDate,
	val appliedDate: LocalDate?,
	val numberOfGames: Int,
	val preBowl: SeriesPreBowl,
	val manualScores: List<Int>?,
	val excludeFromStatistics: ExcludeFromStatistics,
	val alleyId: UUID?,
)

data class SeriesUpdate(
	val id: SeriesID,
	val date: LocalDate,
	val appliedDate: LocalDate?,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
	val alleyId: UUID?,
)

enum class SeriesPreBowl {
	REGULAR,
	PRE_BOWL,
}
