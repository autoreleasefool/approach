package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.Game
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import java.util.UUID

@Entity(
	tableName = "games",
)
data class GameEntity(
	@PrimaryKey val id: UUID,
	val seriesId: UUID,
	val index: Int,
	val score: Int,
	val locked: GameLockState,
	val scoringMethod: GameScoringMethod,
	val excludeFromStatistics: ExcludeFromStatistics,
)

fun GameEntity.asExternalModel() = Game(
	id = id,
	index = index,
	score = score,
	locked = locked,
	scoringMethod = scoringMethod,
	excludeFromStatistics = excludeFromStatistics,
)