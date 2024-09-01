package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import ca.josephroque.bowlingcompanion.core.model.utils.SortableByUUID
import java.util.UUID
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class AlleyID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): AlleyID = AlleyID(UUID.randomUUID())
		fun fromString(string: String): AlleyID = AlleyID(UUID.fromString(string))
	}
}

data class AlleyListItem(
	val alleyId: AlleyID,
	val name: String,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
) : SortableByUUID {
	override val id: UUID
		get() = alleyId.value
}

data class AlleyDetails(
	val id: AlleyID,
	val name: String,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
)

data class AlleyCreate(
	val id: AlleyID,
	val name: String,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
	val lanes: List<LaneListItem>,
)

data class AlleyUpdate(
	val id: AlleyID,
	val name: String,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
	val lanes: List<LaneListItem>,
) {
	data class Properties(
		val id: AlleyID,
		val name: String,
		val material: AlleyMaterial?,
		val pinFall: AlleyPinFall?,
		val mechanism: AlleyMechanism?,
		val pinBase: AlleyPinBase?,
	)
}

enum class AlleyMaterial {
	SYNTHETIC,
	WOOD,
}

enum class AlleyMechanism {
	DEDICATED,
	INTERCHANGEABLE,
}

enum class AlleyPinBase {
	BLACK,
	WHITE,
	OTHER,
}

enum class AlleyPinFall {
	FREE_FALL,
	STRINGS,
}
