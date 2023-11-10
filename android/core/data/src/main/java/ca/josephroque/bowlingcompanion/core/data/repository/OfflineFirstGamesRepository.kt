package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.model.GameEdit
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class OfflineFirstGamesRepository @Inject constructor(
	private val gameDao: GameDao,
): GamesRepository {
	override fun getGameDetails(gameId: UUID): Flow<GameEdit> =
		gameDao.getGameDetails(gameId).map { it.asModel() }

	override fun getGamesList(seriesId: UUID): Flow<List<GameListItem>> =
		gameDao.getGamesList(seriesId)

	override fun getGameIndex(gameId: UUID): Flow<Int> =
		gameDao.getGameIndex(gameId)
}