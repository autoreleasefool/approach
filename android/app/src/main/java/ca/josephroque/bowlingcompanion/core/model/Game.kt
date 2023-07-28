package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class Game(
	val id: UUID,
	val index: Int,
	val score: Int,
	val locked: GameLockState,
	val scoringMethod: GameScoringMethod,
	val excludeFromStatistics: ExcludeFromStatistics,
)

enum class GameLockState {
	LOCKED,
	UNLOCKED,
}

enum class GameScoringMethod {
	MANUAL,
	BY_FRAME,
}