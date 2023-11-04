package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
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
)

data class GameEditEntity(
	@Embedded val properties: GameEdit.Properties,
	@Embedded(prefix = "series_") val series: GameEdit.Series,
	@Embedded(prefix = "league_") val league: GameEdit.League,
	@Embedded(prefix = "bowler_") val bowler: GameEdit.Bowler,
)

fun GameEditEntity.asModel(): GameEdit = GameEdit(
	properties = this.properties,
	series = this.series,
	league = this.league,
	bowler = this.bowler
)