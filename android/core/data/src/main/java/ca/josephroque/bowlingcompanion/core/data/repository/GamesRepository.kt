package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.GameCreate
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameInProgress
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import ca.josephroque.bowlingcompanion.core.model.GameLockState
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface GamesRepository {
	fun getArchivedGames(): Flow<List<ArchivedGame>>

	fun getGameDetails(gameId: UUID): Flow<GameEdit>
	fun getGamesList(seriesId: UUID): Flow<List<GameListItem>>
	fun getGameIds(seriesId: UUID): Flow<List<UUID>>
	fun getGameIndex(gameId: UUID): Flow<Int>

	suspend fun isGameInProgress(): Boolean
	suspend fun getGameInProgress(): GameInProgress?

	suspend fun setGameScoringMethod(gameId: UUID, scoringMethod: GameScoringMethod, score: Int)
	suspend fun setGameLockState(gameId: UUID, locked: GameLockState)
	suspend fun setGameExcludedFromStatistics(
		gameId: UUID,
		excludeFromStatistics: ExcludeFromStatistics,
	)
	suspend fun setGameScore(gameId: UUID, score: Int)
	suspend fun setGameLanes(gameId: UUID, lanes: Set<UUID>)
	suspend fun setAllGameLanes(seriesId: UUID, lanes: Set<UUID>)

	suspend fun insertGames(games: List<GameCreate>)
	suspend fun archiveGame(gameId: UUID)
	suspend fun unarchiveGame(gameId: UUID)
}
