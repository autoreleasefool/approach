package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class Bowler(
	val id: UUID,
	val name: String,
	val kind: BowlerKind,
)

data class BowlerUpdate(
	val id: UUID,
	val name: String,
)

enum class BowlerKind {
	PLAYABLE,
	OPPONENT,
}