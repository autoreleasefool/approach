package ca.josephroque.bowlingcompanion.core.model

import androidx.room.Embedded
import kotlinx.datetime.LocalDate
import java.util.UUID

data class GameDetails(
	@Embedded val properties: GameDetailsProperties,
	@Embedded(prefix = "series_") val series: GameDetailsSeriesProperties,
	@Embedded(prefix = "league_") val league: GameDetailsLeagueProperties,
	@Embedded(prefix = "bowler_") val bowler: GameDetailsBowlerProperties,
)

data class GameDetailsProperties(
	val id: UUID,
	val index: Int,
	val score: Int,
	val locked: GameLockState,
	val scoringMethod: GameScoringMethod,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameDetailsSeriesProperties(
	val date: LocalDate,
	val preBowl: SeriesPreBowl,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameDetailsLeagueProperties(
	val name: String,
	val excludeFromStatistics: ExcludeFromStatistics,
)

data class GameDetailsBowlerProperties(
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