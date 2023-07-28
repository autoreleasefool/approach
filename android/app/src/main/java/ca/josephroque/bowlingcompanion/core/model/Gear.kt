package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class Gear(
	val id: UUID,
	val name: String,
	val kind: GearKind,
	val ownerId: UUID?,
)

enum class GearKind {
	SHOES,
	BOWLING_BALL,
	TOWEL,
	OTHER,
}