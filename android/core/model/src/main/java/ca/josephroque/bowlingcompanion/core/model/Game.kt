package ca.josephroque.bowlingcompanion.core.model

import kotlinx.datetime.LocalDate
import java.util.UUID

object Game {
	const val NumberOfFrames = 10
	val FrameIndices = 0..<NumberOfFrames
	const val FoulPenalty = 15

	fun frameIndicesAfter(after: Int, upTo: Int = NumberOfFrames): IntRange =
		(after + 1)..<upTo
}

data class GameEdit(
	val properties: GameEditProperties,
	val series: GameEditSeriesProperties,
	val league: GameEditLeagueProperties,
	val bowler: GameEditBowlerProperties,
)

data class GameEditProperties(
	val id: UUID,
	val index: Int,
	val score: Int,
	val locked: GameLockState,
	val scoringMethod: GameScoringMethod,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameEditSeriesProperties(
	val date: LocalDate,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameEditLeagueProperties(
	val name: String,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameEditBowlerProperties(
	val name: String
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