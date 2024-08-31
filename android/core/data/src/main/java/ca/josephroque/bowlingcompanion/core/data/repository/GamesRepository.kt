package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameCreate
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameInProgress
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import kotlinx.coroutines.flow.Flow

interface GamesRepository {
	fun getArchivedGames(): Flow<List<ArchivedGame>>

	fun getGameDetails(gameId: GameID): Flow<GameEdit>
	fun getGamesList(seriesId: SeriesID): Flow<List<GameListItem>>
	fun getGameIds(seriesId: SeriesID): Flow<List<GameID>>
	fun getGameIndex(gameId: GameID): Flow<Int>
	fun getTeamSeriesGameIds(teamSeriesId: TeamSeriesID): Flow<List<GameID>>

	suspend fun isGameInProgress(): Boolean
	suspend fun getGameInProgress(): GameInProgress?

	suspend fun setGameScoringMethod(gameId: GameID, scoringMethod: GameScoringMethod, score: Int)
	suspend fun setGameLockState(gameId: GameID, locked: GameLockState)
	suspend fun setGameExcludedFromStatistics(
		gameId: GameID,
		excludeFromStatistics: ExcludeFromStatistics,
	)
	suspend fun setGameScore(gameId: GameID, score: Int)
	suspend fun setGameLanes(gameId: GameID, lanes: Set<LaneID>)
	suspend fun setGameDuration(gameId: GameID, durationMillis: Long)
	suspend fun setAllGameLanes(seriesId: SeriesID, lanes: Set<LaneID>)

	suspend fun insertGames(games: List<GameCreate>)
	suspend fun archiveGame(gameId: GameID)
	suspend fun unarchiveGame(gameId: GameID)

	suspend fun lockStaleGames()
}
