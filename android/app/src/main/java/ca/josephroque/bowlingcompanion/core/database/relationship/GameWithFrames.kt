package ca.josephroque.bowlingcompanion.core.database.relationship

import androidx.room.Embedded
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.database.model.FrameEntity
import ca.josephroque.bowlingcompanion.core.database.model.GameEntity

data class GameWithFrames(
	@Embedded val game: GameEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "gameId",
	)
	val frames: List<FrameEntity>
)