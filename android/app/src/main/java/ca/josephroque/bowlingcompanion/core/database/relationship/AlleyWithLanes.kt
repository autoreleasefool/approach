package ca.josephroque.bowlingcompanion.core.database.relationship

import androidx.room.Embedded
import androidx.room.Relation
import ca.josephroque.bowlingcompanion.core.database.model.AlleyEntity
import ca.josephroque.bowlingcompanion.core.database.model.LaneEntity

data class AlleyWithLanes(
	@Embedded val alley: AlleyEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "alleyId",
	)
	val lanes: List<LaneEntity>
)