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

data class TrackableGame(
	val seriesId: UUID,
	val id: UUID,
	val index: Int,
	val score: Int,
	val date: LocalDate,
	val matchPlay: MatchPlay,
) {
	data class MatchPlay(
		val id: UUID,
		val result: MatchPlayResult?,
	)
}

data class GameEdit(
	val properties: Properties,
	val series: Series,
	val league: League,
	val bowler: Bowler,
) {
	data class Properties(
		val id: UUID,
		val index: Int,
		val score: Int,
		val locked: GameLockState,
		val scoringMethod: GameScoringMethod,
		val excludeFromStatistics: ExcludeFromStatistics,
	)

	data class Series(
		val date: LocalDate,
		val preBowl: SeriesPreBowl,
		val excludeFromStatistics: ExcludeFromStatistics,
	)

	data class League(
		val name: String,
		val excludeFromStatistics: ExcludeFromStatistics,
	)

	data class Bowler(
		val name: String,
	)
}

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