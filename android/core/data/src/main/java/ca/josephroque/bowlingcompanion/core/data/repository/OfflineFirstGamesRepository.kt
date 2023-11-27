package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.model.ArchivedGame
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class OfflineFirstGamesRepository @Inject constructor(
	private val gameDao: GameDao,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): GamesRepository {
	override fun getGameDetails(gameId: UUID): Flow<GameEdit> =
		gameDao.getGameDetails(gameId).map { it.asModel() }

	override fun getGamesList(seriesId: UUID): Flow<List<GameListItem>> =
		gameDao.getGamesList(seriesId)

	override fun getArchivedGames(): Flow<List<ArchivedGame>> =
		gameDao.getArchivedGames()

	override fun getGameIndex(gameId: UUID): Flow<Int> =
		gameDao.getGameIndex(gameId)

	override suspend fun archiveGame(gameId: UUID) = withContext(ioDispatcher) {
		gameDao.archiveGame(gameId)
	}

	override suspend fun unarchiveGame(gameId: UUID) = withContext(ioDispatcher) {
		gameDao.unarchiveGame(gameId)
	}
}