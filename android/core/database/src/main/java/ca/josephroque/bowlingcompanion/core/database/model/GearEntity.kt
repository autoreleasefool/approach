package ca.josephroque.bowlingcompanion.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.GearCreate
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearUpdate
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
data class GearEntity(
	@PrimaryKey @ColumnInfo(name = "id", index = true) val id: UUID,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "kind") val kind: GearKind,
	@ColumnInfo(name = "avatar") val avatar: Avatar,
	@ColumnInfo(name = "owner_id", index = true) val ownerId: UUID?,
)

data class GearCreateEntity(
	val id: UUID,
	val name: String,
	val kind: GearKind,
	val avatar: Avatar,
	@ColumnInfo(name = "owner_id") val ownerId: UUID?,
)

fun GearCreate.asEntity(): GearCreateEntity = GearCreateEntity(
	id = id,
	name = name,
	kind = kind,
	avatar = avatar,
	ownerId = ownerId,
)

data class GearUpdateEntity(
	val id: UUID,
	val name: String,
	@ColumnInfo(name = "owner_id") val ownerId: UUID?,
	val kind: GearKind,
	val avatar: Avatar
) {
	fun asModel(): GearUpdate = GearUpdate(
		id = id,
		name = name,
		ownerId = ownerId,
		kind = kind,
		avatar = avatar,
	)
}

fun GearUpdate.asEntity(): GearUpdateEntity = GearUpdateEntity(
	id = id,
	name = name,
	ownerId = ownerId,
	kind = kind,
	avatar = avatar,
)