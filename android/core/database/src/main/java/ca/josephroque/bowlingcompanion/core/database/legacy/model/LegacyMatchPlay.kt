package ca.josephroque.bowlingcompanion.core.database.legacy.model

data class LegacyMatchPlay(
	val id: Long,
	val opponentScore: Int,
	val opponentName: String?,
	val gameId: Long,
)