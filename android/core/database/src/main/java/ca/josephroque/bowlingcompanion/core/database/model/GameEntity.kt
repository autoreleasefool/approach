package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameCreate
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import java.util.UUID
import kotlinx.datetime.Instant
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
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
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
	val id: UUID,
	val index: Int,
	val score: Int,
	val date: LocalDate,
	@Embedded(prefix = "match_play_") val matchPlay: TrackableGame.MatchPlay?,
) {
	fun asModel(): TrackableGame = TrackableGame(
		seriesId = this.seriesId,
		id = this.id,
		index = this.index,
		score = this.score,
		date = this.date,
		matchPlay = this.matchPlay,
	)
}

data class GameEditEntity(
	@Embedded val properties: GameEdit.Properties,
	@Embedded(prefix = "series_") val series: GameEdit.Series,
	@Embedded(prefix = "league_") val league: GameEdit.League,
	@Embedded(prefix = "bowler_") val bowler: GameEdit.Bowler,
) {

	fun asModel(): GameEdit = GameEdit(
		properties = this.properties,
		series = this.series,
		league = this.league,
		bowler = this.bowler,
	)
}
