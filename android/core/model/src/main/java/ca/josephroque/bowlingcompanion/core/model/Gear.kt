package ca.josephroque.bowlingcompanion.core.model

import ca.josephroque.bowlingcompanion.core.model.utils.SortableByUUID
import java.util.UUID

data class GearListItem(
	override val id: UUID,
	val name: String,
	val kind: GearKind,
	val ownerName: String?,
	val avatar: Avatar,
) : SortableByUUID

data class GearDetails(val id: UUID, val name: String, val kind: GearKind, val avatar: Avatar)

data class GearCreate(
	val id: UUID,
	val name: String,
	val kind: GearKind,
	val avatar: Avatar,
	val ownerId: BowlerID?,
)

data class GearUpdate(
	val id: UUID,
	val name: String,
	val kind: GearKind,
	val avatar: Avatar,
	val ownerId: BowlerID?,
)

enum class GearKind {
	SHOES,
	BOWLING_BALL,
	TOWEL,
	OTHER,
}
