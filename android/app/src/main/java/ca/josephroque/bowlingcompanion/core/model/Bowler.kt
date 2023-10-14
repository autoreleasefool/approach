package ca.josephroque.bowlingcompanion.core.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@Immutable
data class BowlerListItem(
	val id: UUID,
	val name: String,
	val average: Double?,
)

@Immutable
data class OpponentListItem(
	val id: UUID,
	val name: String,
	val kind: BowlerKind,
)

@Immutable
data class BowlerDetails(
	val id: UUID,
	val name: String,
)

enum class BowlerKind {
	PLAYABLE,
	OPPONENT,
}