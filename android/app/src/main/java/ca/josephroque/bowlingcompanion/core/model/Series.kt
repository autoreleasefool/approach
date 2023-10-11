package ca.josephroque.bowlingcompanion.core.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity
import kotlinx.datetime.LocalDate
import java.util.UUID

data class SeriesDetails(
	@Embedded
	val details: SeriesDetailsProperties,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
	)
	val scores: List<SeriesScore>,
)

data class SeriesDetailsProperties(
	val id: UUID,
	val date: LocalDate,
	val total: Int,
	@ColumnInfo(name = "number_of_games") val numberOfGames: Int,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

data class SeriesCreate(
	@ColumnInfo(name = "league_id") val leagueId: UUID,
	val id: UUID,
	val date: LocalDate,
	@ColumnInfo(name = "number_of_games") val numberOfGames: Int,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

data class SeriesUpdate(
	val id: UUID,
	val date: LocalDate,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

data class SeriesListItem(
	@Embedded
	val series: SeriesListProperties,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
	)
	val scores: List<SeriesScore>,
)

data class SeriesListProperties(
	val id: UUID,
	val date: LocalDate,
	val total: Int,
	val preBowl: SeriesPreBowl,
)

data class SeriesScore(
	val score: Int,
)

enum class SeriesPreBowl {
	REGULAR,
	PRE_BOWL,
}