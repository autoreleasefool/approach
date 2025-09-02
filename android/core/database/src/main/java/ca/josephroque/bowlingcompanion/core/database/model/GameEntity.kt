package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameCreate
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.MatchPlayID
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

@Entity(
	tableName = "games",
	foreignKeys = [
		ForeignKey(
			entity = SeriesEntity::class,
			parentColumns = ["id"],
			childColumns = ["series_id"],
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE,
		),
	],
)
data class GameEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: GameID,
	@ColumnInfo(name = "series_id", index = true) val seriesId: SeriesID,
	@ColumnInfo(name = "index") val index: Int,
	@ColumnInfo(name = "score") val score: Int,
	@ColumnInfo(name = "locked") val locked: GameLockState,
	@ColumnInfo(name = "scoring_method") val scoringMethod: GameScoringMethod,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	@ColumnInfo(name = "archived_on", defaultValue = "NULL") val archivedOn: Instant? = null,
	@ColumnInfo(name = "durationMillis", defaultValue = "0") val durationMillis: Long = 0,
)

fun GameCreate.asEntity(): GameEntity = GameEntity(
	id = id,
	seriesId = seriesId,
	index = index,
	score = score,
	locked = locked,
	scoringMethod = scoringMethod,
	excludeFromStatistics = excludeFromStatistics,
)

data class TrackableGameEntity(
	val seriesId: SeriesID,
	val id: GameID,
	val index: Int,
	val score: Int,
	val date: LocalDate,
	@Embedded(prefix = "match_play_") val matchPlay: TrackableGameMatchPlayEntity?,
) {
	fun asModel(): TrackableGame = TrackableGame(
		seriesId = this.seriesId,
		id = this.id,
		index = this.index,
		score = this.score,
		date = this.date,
		matchPlay = this.matchPlay?.asModel(),
	)
}

data class TrackableGameMatchPlayEntity(val id: MatchPlayID, val result: MatchPlayResult?) {
	fun asModel() = TrackableGame.MatchPlay(id = id, result = result)
}

data class GameEditEntity(
	@Embedded val properties: GameEditPropertiesEntity,
	@Embedded(prefix = "series_") val series: GameEditSeriesEntity,
	@Embedded(prefix = "league_") val league: GameEditLeagueEntity,
	@Embedded(prefix = "bowler_") val bowler: GameEditBowlerEntity,
) {
	fun asModel() = GameEdit(
		properties = this.properties.asModel(),
		series = this.series.asModel(),
		league = this.league.asModel(),
		bowler = this.bowler.asModel(),
	)
}

data class GameEditPropertiesEntity(
	val id: GameID,
	val index: Int,
	val score: Int,
	val locked: GameLockState,
	@ColumnInfo(name = "scoring_method") val scoringMethod: GameScoringMethod,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	val durationMillis: Long,
) {
	fun asModel() = GameEdit.Properties(
		id = id,
		index = index,
		score = score,
		locked = locked,
		scoringMethod = scoringMethod,
		excludeFromStatistics = excludeFromStatistics,
		durationMillis = durationMillis,
	)
}

data class GameEditSeriesEntity(
	val id: SeriesID,
	val date: LocalDate,
	@ColumnInfo(name = "pre_bowl") val preBowl: SeriesPreBowl,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
) {
	fun asModel() = GameEdit.Series(id = id, date = date, preBowl = preBowl, excludeFromStatistics = excludeFromStatistics)
}

data class GameEditLeagueEntity(
	val id: LeagueID,
	val name: String,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
) {
	fun asModel() = GameEdit.League(id = id, name = name, excludeFromStatistics = excludeFromStatistics)
}

data class GameEditBowlerEntity(val id: BowlerID, val name: String) {
	fun asModel() = GameEdit.Bowler(id = id, name = name)
}

data class ShareableGameEntity(
	val id: GameID,
	val index: Int,
	val bowlerName: String,
	val leagueName: String,
	val seriesDate: LocalDate,
	val alleyName: String?,
)
