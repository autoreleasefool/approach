package ca.josephroque.bowlingcompanion.core.database.legacy.model

import ca.josephroque.bowlingcompanion.core.model.MatchPlayResult

data class LegacyGame(
	val id: Long,
	val gameNumber: Int,
	val score: Int,
	val isLocked: Boolean,
	val isManual: Boolean,
	val matchPlayResult: LegacyMatchPlayResult,
	val seriesId: Long,
)

enum class LegacyMatchPlayResult {
	NONE,
	WON,
	LOST,
	TIED;

	companion object {
		private val map = LegacyMatchPlayResult.values().associateBy(LegacyMatchPlayResult::ordinal)
		fun fromInt(type: Int) = map[type]
	}
}

fun LegacyMatchPlayResult.asMatchPlay() = when (this) {
	LegacyMatchPlayResult.NONE -> null
	LegacyMatchPlayResult.WON -> MatchPlayResult.WON
	LegacyMatchPlayResult.LOST -> MatchPlayResult.LOST
	LegacyMatchPlayResult.TIED -> MatchPlayResult.TIED
}