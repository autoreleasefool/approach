package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.GameDao
import ca.josephroque.bowlingcompanion.core.model.GameDetails
import ca.josephroque.bowlingcompanion.core.model.GameListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class OfflineFirstGamesRepository @Inject constructor(
	private val gameDao: GameDao,
): GamesRepository {
	override fun getGameDetails(gameId: UUID): Flow<GameDetails> =
		gameDao.getGameDetails(gameId)

	override fun getGamesList(seriesId: UUID): Flow<List<GameListItem>> =
		gameDao.getGamesList(seriesId)
}