package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Lane
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import java.util.UUID

@Entity(
	tableName = "lanes",
)
data class LaneEntity(
	@PrimaryKey val id: UUID,
	val alleyId: UUID,
	val label: String,
	val position: LanePosition,
)

fun LaneEntity.asExternalModel() = Lane(
	id = id,
	label = label,
	position = position,
)