package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class BowlerWithGear(
	@Embedded
	val bowler: BowlerEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "ownerId",
	)
	val gear: List<GearEntity>
)