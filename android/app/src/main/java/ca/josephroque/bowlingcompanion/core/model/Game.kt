package ca.josephroque.bowlingcompanion.core.model

import androidx.room.Embedded
import java.util.UUID

data class GameDetails(
	@Embedded val details: GameDetailsProperties,
)

data class GameDetailsProperties(
	val id: UUID,
	val index: Int,
	val score: Int,
	val locked: GameLockState,
	val scoringMethod: GameScoringMethod,
	val excludeFromStatistics: ExcludeFromStatistics,
)

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