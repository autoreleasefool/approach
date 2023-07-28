package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class Alley(
	val id: UUID,
	val name: String,
	val material: AlleyMaterial?,
	val pinFall: AlleyPinFall?,
	val mechanism: AlleyMechanism?,
	val pinBase: AlleyPinBase?,
)

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