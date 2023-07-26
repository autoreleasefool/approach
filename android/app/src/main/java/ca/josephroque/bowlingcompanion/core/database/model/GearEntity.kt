package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Gear
import ca.josephroque.bowlingcompanion.core.model.GearKind
import java.util.UUID

@Entity(
	tableName = "gear",
)
data class GearEntity(
	@PrimaryKey val id: UUID,
	val name: String,
	val kind: GearKind,
	val ownerId: UUID?,
)

fun GearEntity.asExternalModel() = Gear(
	id = id,
	name = name,
	kind = kind,
	ownerId = ownerId,
)