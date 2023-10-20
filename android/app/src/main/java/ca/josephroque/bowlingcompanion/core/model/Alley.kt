package ca.josephroque.bowlingcompanion.core.model

import androidx.compose.runtime.Immutable
import ca.josephroque.bowlingcompanion.utils.SortableByUUID
import java.util.UUID

@Immutable
data class AlleyDetails(
	val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
	val numberOfLanes: Int,
)

@Immutable
data class AlleyListItem(
	override val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
): SortableByUUID

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