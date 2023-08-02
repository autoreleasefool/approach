package ca.josephroque.bowlingcompanion.core.database.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.Game
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
@Immutable
data class GameEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "series_id", index = true) val seriesId: UUID,
	@ColumnInfo(name = "index") val index: Int,
	@ColumnInfo(name = "score") val score: Int,
	@ColumnInfo(name = "locked") val locked: GameLockState,
	@ColumnInfo(name = "scoring_method") val scoringMethod: GameScoringMethod,
	@ColumnInfo(name = "exclude_from_statistics") val excludeFromStatistics: ExcludeFromStatistics,
)

fun GameEntity.asExternalModel() = Game(
	id = id,
	index = index,
	score = score,
	locked = locked,
	scoringMethod = scoringMethod,
	excludeFromStatistics = excludeFromStatistics,
)