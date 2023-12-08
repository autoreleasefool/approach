package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.BowlerSummary
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult
import ca.josephroque.bowlingcompanion.core.model.TrackableGame
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.util.UUID

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
	@ColumnInfo(name = "series_id", index = true) val seriesId: UUID,
	@ColumnInfo(name = "index") val index: Int,
	@ColumnInfo(name = "score") val score: Int,
	@ColumnInfo(name = "locked") val locked: GameLockState,
	@ColumnInfo(name = "scoring_method") val scoringMethod: GameScoringMethod,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
	@ColumnInfo(name = "archived_on", defaultValue = "NULL") val archivedOn: Instant? = null,
	@ColumnInfo(name = "duration", defaultValue = "0") val duration: Double = 0.0,
)

data class TrackableGameEntity(
	val seriesId: UUID,
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
	@Embedded(prefix = "match_play_") val matchPlay: MatchPlayEntity?,
) {
	data class MatchPlayEntity(
		@Embedded(prefix = "bowler_") val opponent: BowlerSummary,
		val opponentScore: Int?,
		val result: MatchPlayResult?,
	)

	fun asModel(): GameEdit = GameEdit(
		properties = this.properties,
		series = this.series,
		league = this.league,
		bowler = this.bowler,
		matchPlay = this.matchPlay?.let {
			GameEdit.MatchPlay(
				opponent = it.opponent,
				opponentScore = it.opponentScore,
				result = it.result,
			)
		},
	)
}