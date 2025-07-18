package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesLeagueUpdate
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesListProperties
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

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
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: SeriesID,
	@ColumnInfo(name = "league_id", index = true) val leagueId: LeagueID,
	@ColumnInfo(name = "date") val date: LocalDate,
	@ColumnInfo(name = "applied_date") val appliedDate: LocalDate? = null,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	@ColumnInfo(name = "alley_id", index = true) val alleyId: AlleyID?,
	@ColumnInfo(name = "archived_on", defaultValue = "NULL") val archivedOn: Instant? = null,
)

data class TrackableSeriesEntity(
	val id: SeriesID,
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
	@ColumnInfo(name = "league_id") val leagueId: LeagueID,
	val id: SeriesID,
	val date: LocalDate,
	@Ignore val numberOfGames: Int,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	@ColumnInfo(name = "alley_id") val alleyId: AlleyID?,
)

fun SeriesCreate.asEntity(): SeriesCreateEntity = SeriesCreateEntity(
	leagueId = leagueId,
	id = id,
	date = date,
	numberOfGames = numberOfGames,
	preBowl = preBowl,
	excludeFromStatistics = excludeFromStatistics,
	alleyId = alleyId,
)

data class SeriesUpdateEntity(
	val id: SeriesID,
	val date: LocalDate,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	@ColumnInfo(name = "applied_date") val appliedDate: LocalDate?,
	@ColumnInfo(name = "alley_id") val alleyId: AlleyID?,
)

fun SeriesUpdate.asEntity(): SeriesUpdateEntity = SeriesUpdateEntity(
	id = id,
	date = date,
	preBowl = preBowl,
	excludeFromStatistics = excludeFromStatistics,
	appliedDate = appliedDate,
	alleyId = alleyId,
)

data class SeriesLeagueUpdateEntity(
	val id: SeriesID,
	@ColumnInfo(name = "league_id") val leagueId: LeagueID,
)

fun SeriesLeagueUpdate.asEntity(): SeriesLeagueUpdateEntity = SeriesLeagueUpdateEntity(
	id = id,
	leagueId = leagueId,
)

data class SeriesDetailsEntity(
	@Embedded val properties: SeriesDetails.Properties,
	@Embedded(prefix = "alley_") val alley: SeriesDetails.Alley?,
	@Embedded(prefix = "league_") val league: SeriesDetails.League,
	@Embedded(prefix = "bowler_") val bowler: SeriesDetails.Bowler,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
		projection = ["score", "archived_on"],
	)
	val games: List<Game>,
) {
	data class Game(
		val score: Int,
		@ColumnInfo(name = "archived_on") val archivedOn: Instant?,
	)

	fun asModel(): SeriesDetails = SeriesDetails(
		properties = properties,
		alley = this.alley,
		league = this.league,
		bowler = this.bowler,
		scores = games.filter { it.archivedOn == null }.map { it.score },
	)
}

data class SeriesListEntity(
	@Embedded
	val properties: SeriesListProperties,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
		projection = ["score", "archived_on"],
	)
	val games: List<Game>,
) {
	data class Game(
		val score: Int,
		@ColumnInfo(name = "archived_on") val archivedOn: Instant?,
	)

	fun asModel(): SeriesListItem = SeriesListItem(
		properties = properties,
		scores = games.filter { it.archivedOn == null }.map { it.score },
	)
}

data class ShareableSeriesEntity(
	@Embedded
	val properties: ShareableSeries.Properties,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
		projection = ["score", "archived_on"],
	)
	val games: List<Game>,
) {
	data class Game(
		val score: Int,
		@ColumnInfo(name = "archived_on") val archivedOn: Instant?,
	)

	fun asModel(): ShareableSeries = ShareableSeries(
		properties = properties,
		scores = games.filter { it.archivedOn == null }.map { it.score },
	)
}
