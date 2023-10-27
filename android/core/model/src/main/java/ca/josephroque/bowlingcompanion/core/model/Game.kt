package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class GameListItem(
	val id: UUID,
	val index: Int,
	val score: Int,
)

enum class GameLockState {
	LOCKED,
	UNLOCKED,
}

enum class GameScoringMethod {
	MANUAL,
	BY_FRAME,
}