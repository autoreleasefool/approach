package ca.josephroque.bowlingcompanion.core.database.legacy.model

data class LegacyFrame(
	val id: Long,
	val gameId: Long,
	val ordinal: Int,
	val isAccessed: Boolean,
	val firstPinState: Int,
	val secondPinState: Int,
	val thirdPinState: Int,
	val fouls: Int,
)
