package ca.josephroque.bowlingcompanion.core.database.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Gear
import ca.josephroque.bowlingcompanion.core.model.GearKind
import java.util.UUID

@Entity(
	tableName = "gear",
	foreignKeys = [
		ForeignKey(
			entity = BowlerEntity::class,
			parentColumns = ["id"],
			childColumns = ["owner_id"],
			onUpdate = ForeignKey.CASCADE,
			onDelete = ForeignKey.SET_NULL,
		)
	],
)
@Immutable
data class GearEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "kind") val kind: GearKind,
	@ColumnInfo(name = "owner_id", index = true) val ownerId: UUID?,
)

fun GearEntity.asExternalModel() = Gear(
	id = id,
	name = name,
	kind = kind,
	ownerId = ownerId,
)