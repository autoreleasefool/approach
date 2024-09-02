package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class LaneID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): LaneID = LaneID(UUID.randomUUID())
		fun fromString(string: String): LaneID = LaneID(UUID.fromString(string))
	}
}

data class LaneListItem(val id: LaneID, val label: String, val position: LanePosition)

enum class LanePosition {
	LEFT_WALL,
	RIGHT_WALL,
	NO_WALL,
}
