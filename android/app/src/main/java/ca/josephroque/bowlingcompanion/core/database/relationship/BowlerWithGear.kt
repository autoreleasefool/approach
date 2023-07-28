package ca.josephroque.bowlingcompanion.core.database.relationship

import androidx.room.Embedded
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.database.model.BowlerEntity
import ca.josephroque.bowlingcompanion.core.database.model.GearEntity

data class BowlerWithGear(
	@Embedded
	val bowler: BowlerEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "ownerId",
	)
	val gear: List<GearEntity>
)