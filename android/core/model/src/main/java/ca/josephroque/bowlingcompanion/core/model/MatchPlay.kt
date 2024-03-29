package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class MatchPlayCreate(
	val id: UUID,
	val gameId: UUID,
	val opponentId: UUID?,
	val opponentScore: Int?,
	val result: MatchPlayResult?,
)

data class MatchPlayUpdate(
	val id: UUID,
	val opponent: BowlerSummary?,
	val opponentScore: Int?,
	val result: MatchPlayResult?,
) {
	data class Properties(
		val id: UUID,
		val opponentId: UUID?,
		val opponentScore: Int?,
		val result: MatchPlayResult?,
	)
}

enum class MatchPlayResult {
	WON,
	LOST,
	TIED,
}
