package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class AlleyWithLanes(
	@Embedded val alley: AlleyEntity,
	@Relation(
		parentColumn = "id",
		entityColumn = "alleyId",
	)
	val lanes: List<LaneEntity>
)