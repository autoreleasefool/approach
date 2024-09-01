package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import ca.josephroque.bowlingcompanion.core.model.utils.SortableByUUID
import java.util.UUID
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class GearID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): GearID = GearID(UUID.randomUUID())
		fun fromString(string: String): GearID = GearID(UUID.fromString(string))
	}
}

data class GearListItem(
	val gearId: GearID,
	val name: String,
	val kind: GearKind,
	val ownerName: String?,
	val avatar: Avatar,
) : SortableByUUID {
	override val id: UUID
		get() = gearId.value
}

data class GearDetails(val id: GearID, val name: String, val kind: GearKind, val avatar: Avatar)

data class GearCreate(
	val id: GearID,
	val name: String,
	val kind: GearKind,
	val avatar: Avatar,
	val ownerId: BowlerID?,
)

data class GearUpdate(
	val id: GearID,
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
