package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.MatchPlayDao
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.MatchPlayCreate
import ca.josephroque.bowlingcompanion.core.model.MatchPlayUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class OfflineFirstMatchPlaysRepository @Inject constructor(
	private val matchPlayDao: MatchPlayDao,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,

	) : MatchPlaysRepository {
	override fun getMatchPlay(gameId: UUID): Flow<MatchPlayUpdate?> =
		matchPlayDao.getMatchPlayForGame(gameId).map { it?.asModel() }

	override suspend fun insertMatchPlay(matchPlay: MatchPlayCreate) = withContext(ioDispatcher) {
		matchPlayDao.insertMatchPlay(matchPlay.asEntity())
	}

	override suspend fun updateMatchPlay(matchPlay: MatchPlayUpdate) = withContext(ioDispatcher) {
		matchPlayDao.updateMatchPlay(matchPlay.asEntity())
	}

	override suspend fun deleteMatchPlay(matchPlayId: UUID) = withContext(ioDispatcher) {
		matchPlayDao.deleteMatchPlay(matchPlayId)
	}
}