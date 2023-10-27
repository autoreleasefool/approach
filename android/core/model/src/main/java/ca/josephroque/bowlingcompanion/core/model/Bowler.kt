package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class BowlerListItem(
	val id: UUID,
	val name: String,
	val average: Double?,
)

data class OpponentListItem(
	val id: UUID,
	val name: String,
	val kind: BowlerKind,
)

data class BowlerDetails(
	val id: UUID,
	val name: String,
)

enum class BowlerKind {
	PLAYABLE,
	OPPONENT,
}