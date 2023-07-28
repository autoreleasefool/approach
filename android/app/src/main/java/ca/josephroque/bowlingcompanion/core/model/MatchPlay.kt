package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID

data class MatchPlay(
	val id: UUID,
)

enum class MatchPlayResult {
	TIED,
	LOST,
	WON,
}