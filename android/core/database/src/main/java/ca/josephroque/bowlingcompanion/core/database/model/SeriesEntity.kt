package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesCreate
import ca.josephroque.bowlingcompanion.core.model.SeriesDetails
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesLeagueUpdate
import ca.josephroque.bowlingcompanion.core.model.SeriesListItem
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesUpdate
import ca.josephroque.bowlingcompanion.core.model.ShareableSeries
import ca.josephroque.bowlingcompanion.core.model.TrackableSeries
import kotlin.time.Instant
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

data class TrackableSeriesEntity(val id: SeriesID, val numberOfGames: Int, val total: Int, val date: LocalDate) {
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

data class SeriesLeagueUpdateEntity(val id: SeriesID, @ColumnInfo(name = "league_id") val leagueId: LeagueID)

fun SeriesLeagueUpdate.asEntity(): SeriesLeagueUpdateEntity = SeriesLeagueUpdateEntity(
	id = id,
	leagueId = leagueId,
)

data class SeriesDetailsEntity(
	@Embedded val properties: SeriesDetailsPropertiesEntity,
	@Embedded(prefix = "alley_") val alley: SeriesDetailsAlleyEntity?,
	@Embedded(prefix = "league_") val league: SeriesDetailsLeagueEntity,
	@Embedded(prefix = "bowler_") val bowler: SeriesDetailsBowlerEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
		projection = ["score", "archived_on"],
	)
	val games: List<SeriesDetailsGameEntity>,
) {
	fun asModel(): SeriesDetails = SeriesDetails(
		properties = properties.asModel(),
		alley = this.alley?.asModel(),
		league = this.league.asModel(),
		bowler = this.bowler.asModel(),
		scores = games.filter { it.archivedOn == null }.map { it.score },
	)
}

data class SeriesDetailsPropertiesEntity(
	val id: SeriesID,
	val date: LocalDate,
	@ColumnInfo(name = "applied_date") val appliedDate: LocalDate?,
	val total: Int,
	@ColumnInfo(name = "number_of_games") val numberOfGames: Int,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
) {
	fun asModel() = SeriesDetails.Properties(
		id = id,
		date = date,
		appliedDate = appliedDate,
		total = total,
		numberOfGames = numberOfGames,
		preBowl = preBowl,
		excludeFromStatistics = excludeFromStatistics,
	)
}

data class SeriesDetailsAlleyEntity(val id: AlleyID, val name: String) {
	fun asModel() = SeriesDetails.Alley(id = id, name = name)
}

data class SeriesDetailsLeagueEntity(val id: LeagueID, val name: String) {
	fun asModel() = SeriesDetails.League(id = id, name = name)
}

data class SeriesDetailsBowlerEntity(val id: BowlerID, val name: String) {
	fun asModel() = SeriesDetails.Bowler(id = id, name = name)
}

data class SeriesDetailsGameEntity(val score: Int, @ColumnInfo(name = "archived_on") val archivedOn: Instant?)

data class SeriesListEntity(
	@Embedded val properties: SeriesListPropertiesEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
		projection = ["score", "archived_on"],
	)
	val games: List<SeriesListGameEntity>,
) {
	fun asModel() = SeriesListItem(
		properties = properties.asModel(),
		scores = games.filter { it.archivedOn == null }.map { it.score },
	)
}

data class SeriesListPropertiesEntity(
	val id: SeriesID,
	val date: LocalDate,
	val total: Int,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "applied_date") val appliedDate: LocalDate?,
) {
	fun asModel() = SeriesListItem.Properties(
		id = id,
		date = date,
		total = total,
		preBowl = preBowl,
		appliedDate = appliedDate,
	)
}

data class SeriesListGameEntity(val score: Int, @ColumnInfo(name = "archived_on") val archivedOn: Instant?)

data class ShareableSeriesEntity(
	@Embedded val properties: ShareableSeriesPropertiesEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "series_id",
		entity = GameEntity::class,
		projection = ["score", "archived_on"],
	)
	val games: List<ShareableSeriesGameEntity>,
) {
	fun asModel(): ShareableSeries = ShareableSeries(
		properties = properties.asModel(),
		scores = games.filter { it.archivedOn == null }.map { it.score },
	)
}

data class ShareableSeriesPropertiesEntity(
	val id: SeriesID,
	val date: LocalDate,
	val total: Int,
	@ColumnInfo(name = "bowler_name") val bowlerName: String,
	@ColumnInfo(name = "league_name") val leagueName: String,
) {
	fun asModel() = ShareableSeries.Properties(
		id = id,
		date = date,
		total = total,
		bowlerName = bowlerName,
		leagueName = leagueName,
	)
}

data class ShareableSeriesGameEntity(val score: Int, @ColumnInfo(name = "archived_on") val archivedOn: Instant?)
