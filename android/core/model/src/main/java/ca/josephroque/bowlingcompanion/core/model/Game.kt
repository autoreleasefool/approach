package ca.josephroque.bowlingcompanion.core.model

import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

object Game {
	const val NUMBER_OF_FRAMES = 10
	val FrameIndices = 0..<NUMBER_OF_FRAMES
	const val FOUL_PENALTY = 15
	const val MAX_SCORE = 450

	fun frameIndicesAfter(after: Int, upTo: Int = NUMBER_OF_FRAMES): IntRange = (after + 1)..<upTo
}

data class GameSummary(val id: UUID, val index: Int, val score: Int)

data class TrackableGame(
	val seriesId: UUID,
	val id: UUID,
	val index: Int,
	val score: Int,
	val date: LocalDate,
	val matchPlay: MatchPlay?,
) {
	data class MatchPlay(val id: UUID, val result: MatchPlayResult?)
}

data class GameCreate(
	val id: UUID,
	val seriesId: UUID,
	val index: Int,
	val score: Int = 0,
	val locked: GameLockState = GameLockState.UNLOCKED,
	val scoringMethod: GameScoringMethod = GameScoringMethod.BY_FRAME,
	val excludeFromStatistics: ExcludeFromStatistics = ExcludeFromStatistics.INCLUDE,
)

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
		val durationMillis: Long,
	)

	data class Series(
		val id: UUID,
		val date: LocalDate,
		val preBowl: SeriesPreBowl,
		val excludeFromStatistics: ExcludeFromStatistics,
	)

	data class League(
		val id: UUID,
		val name: String,
		val excludeFromStatistics: ExcludeFromStatistics,
	) {
		fun toSummary(): LeagueSummary = LeagueSummary(id = id, name = name)
	}

	data class Bowler(val id: BowlerID, val name: String) {
		fun toSummary(): BowlerSummary = BowlerSummary(id = id, name = name)
	}
}

data class GameListItem(val id: UUID, val index: Int, val score: Int)

data class ArchivedGame(
	val id: UUID,
	val scoringMethod: GameScoringMethod,
	val score: Int,
	val bowlerName: String,
	val leagueName: String,
	val seriesDate: LocalDate,
	val archivedOn: Instant,
)

enum class GameLockState {
	LOCKED,
	UNLOCKED,
	;

	val next: GameLockState
		get() = when (this) {
			LOCKED -> UNLOCKED
			UNLOCKED -> LOCKED
		}
}

enum class GameScoringMethod {
	MANUAL,
	BY_FRAME,
}

data class GameInProgress(val seriesIds: List<UUID>, val currentGameId: UUID)
