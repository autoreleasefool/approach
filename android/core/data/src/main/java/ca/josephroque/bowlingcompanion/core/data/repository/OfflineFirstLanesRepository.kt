package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers.IO
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.LaneDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OfflineFirstLanesRepository @Inject constructor(
	private val laneDao: LaneDao,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : LanesRepository {
	override fun getAlleyLanes(alleyId: AlleyID): Flow<List<LaneListItem>> =
		laneDao.getAlleyLanes(alleyId)

	override fun getLanes(ids: List<LaneID>): Flow<List<LaneListItem>> = laneDao.getLanes(ids)

	override fun getGameLanes(gameId: GameID): Flow<List<LaneListItem>> = laneDao.getGameLanes(gameId)

	override suspend fun insertLanes(lanes: List<LaneListItem>) = withContext(ioDispatcher) {
		laneDao.insertAll(lanes.map(LaneListItem::asEntity))
	}

	override suspend fun setAlleyLanes(alleyId: AlleyID, lanes: List<LaneListItem>) =
		withContext(ioDispatcher) {
			transactionRunner {
				laneDao.deleteAlleyLanes(alleyId)
				laneDao.insertAll(lanes.map { it.asEntity(alleyId) })
			}
		}
}
