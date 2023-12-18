package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.model.AlleyDetails
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesDetailsProperties
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesListProperties
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.util.UUID

@Entity(
	tableName = "series",
	foreignKeys = [
		ForeignKey(
			entity = LeagueEntity::class,
			parentColumns = ["id"],
			childColumns = ["league_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = AlleyEntity::class,
			parentColumns = ["id"],
			childColumns = ["alley_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.SET_NULL,
		),
	],
)
data class SeriesEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "league_id", index = true) val leagueId: UUID,
	@ColumnInfo(name = "date") val date: LocalDate,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	@ColumnInfo(name = "alley_id", index = true) val alleyId: UUID?,
	@ColumnInfo(name = "archived_on", defaultValue = "NULL") val archivedOn: Instant? = null,
)

data class TrackableSeriesEntity(
	val id: UUID,
	val numberOfGames: Int,
	val total: Int,
	val date: LocalDate,
) {
	fun asModel(): TrackableSeries = TrackableSeries(
		id = this.id,
		numberOfGames = this.numberOfGames,
		total = this.total,
		date = this.date,
	)

}

data class SeriesCreateEntity(
	@ColumnInfo(name = "league_id") val leagueId: UUID,
	val id: UUID,
	val date: LocalDate,
	@Ignore val numberOfGames: Int,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

fun SeriesCreate.asEntity(): SeriesCreateEntity = SeriesCreateEntity(
	leagueId = leagueId,
	id = id,
	date = date,
	numberOfGames = numberOfGames,
	preBowl = preBowl,
	excludeFromStatistics = excludeFromStatistics,
)

data class SeriesUpdateEntity(
	val id: UUID,
	val date: LocalDate,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

fun SeriesUpdate.asEntity(): SeriesUpdateEntity = SeriesUpdateEntity(
	id = id,
	date = date,
	preBowl = preBowl,
	excludeFromStatistics = excludeFromStatistics,
)

data class SeriesDetailsEntity(
	@Embedded
	val properties: SeriesDetailsProperties,
	@Relation(
		parentColumn = "alleyId",
		entityColumn = "id",
		entity = AlleyEntity::class,
	)
	val alley: AlleyDetailsEntity?,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
		projection = ["score"],
	)
	val scores: List<Int>,
) {
	fun asModel(): SeriesDetails = SeriesDetails(
		properties = properties,
		scores = scores,
		alley = alley?.asModel(),
	)
}

data class SeriesListEntity(
	@Embedded
	val properties: SeriesListProperties,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
		projection = ["score"],
	)
	val scores: List<Int>,
) {
	fun asModel(): SeriesListItem = SeriesListItem(
		properties = properties,
		scores = scores,
	)
}