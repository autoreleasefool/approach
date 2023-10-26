package ca.josephroque.bowlingcompanion.core.model

import androidx.compose.runtime.Immutable
import java.util.UUID

@JvmInline
@Par
inline value class BowlerID(val value: UUID) {
	companion object {
		fun random(): BowlerID = BowlerID(UUID.randomUUID())
		fun fromString(string: String?) = BowlerID(UUID.fromString(string))
	}
}

@Immutable
data class BowlerListItem(
	val id: BowlerID,
	val name: String,
	val average: Double?,
)

@Immutable
data class OpponentListItem(
	val id: BowlerID,
	val name: String,
	val kind: BowlerKind,
)

@Immutable
data class BowlerDetails(
	val id: BowlerID,
	val name: String,
)

enum class BowlerKind {
	PLAYABLE,
	OPPONENT,
}