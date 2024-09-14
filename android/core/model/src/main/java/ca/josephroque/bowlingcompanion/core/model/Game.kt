package ca.josephroque.bowlingcompanion.core.model

import android.os.Parcelable
import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.parcelize.Parcelize

object Game {
	const val NUMBER_OF_FRAMES = 10
	val FrameIndices = 0..<NUMBER_OF_FRAMES
	const val FOUL_PENALTY = 15
	const val MAX_SCORE = 450

	fun frameIndicesAfter(after: Int, upTo: Int = NUMBER_OF_FRAMES): IntRange = (after + 1)..<upTo
}

@JvmInline
@Parcelize
value class GameID(val value: UUID) : Parcelable {
	override fun toString(): String = value.toString()
	companion object {
		fun randomID(): GameID = GameID(UUID.randomUUID())
		fun fromString(string: String): GameID = GameID(UUID.fromString(string))
	}
} data class GameSummary(val id: GameID, val index: Int, val score: Int)

data class TrackableGame(
	val seriesId: SeriesID,
	val id: GameID,
	val index: Int,
	val score: Int,
	val date: LocalDate,
	val matchPlay: MatchPlay?,
) {
	data class MatchPlay(val id: MatchPlayID, val result: MatchPlayResult?)
}

data class GameCreate(
	val id: GameID,
	val seriesId: SeriesID,
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
		val id: GameID,
		val index: Int,
		val score: Int,
		val locked: GameLockState,
		val scoringMethod: GameScoringMethod,
		val excludeFromStatistics: ExcludeFromStatistics,
		val durationMillis: Long,
	)

	data class Series(
		val id: SeriesID,
		val date: LocalDate,
		val preBowl: SeriesPreBowl,
		val excludeFromStatistics: ExcludeFromStatistics,
	)

	data class League(
		val id: LeagueID,
		val name: String,
		val excludeFromStatistics: ExcludeFromStatistics,
	) {
		fun toSummary(): LeagueSummary = LeagueSummary(id = id, name = name)
	}

	data class Bowler(val id: BowlerID, val name: String) {
		fun toSummary(): BowlerSummary = BowlerSummary(id = id, name = name)
	}
}

data class GameListItem(val id: GameID, val index: Int, val score: Int)

data class GameListItemBySeries(val seriesId: SeriesID, val id: GameID, val index: Int)

data class ArchivedGame(
	val id: GameID,
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

data class GameInProgress(
	val teamSeriesId: TeamSeriesID?,
	val seriesIds: List<SeriesID>,
	val currentGameId: GameID,
)
