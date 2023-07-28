package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import java.util.UUID

@Entity(
	tableName = "frames",
	primaryKeys = ["gameId", "index"],
)
data class FrameEntity(
	val gameId: UUID,
	val index: Int,
	val roll0: String?,
	val roll1: String?,
	val roll2: String?,
	val ball0: UUID?,
	val ball1: UUID?,
	val ball2: UUID?,
)