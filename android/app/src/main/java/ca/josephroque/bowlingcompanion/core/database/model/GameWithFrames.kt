package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class GameWithFrames(
	@Embedded val game: GameEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "gameId",
	)
	val frames: List<FrameEntity>
)