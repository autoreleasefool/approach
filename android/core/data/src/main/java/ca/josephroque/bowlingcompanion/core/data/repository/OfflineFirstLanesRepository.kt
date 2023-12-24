package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.LaneDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class OfflineFirstLanesRepository @Inject constructor(
	private val laneDao: LaneDao,
	private val transactionRunner: TransactionRunner,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): LanesRepository {
	override fun getAlleyLanes(alleyId: UUID): Flow<List<LaneListItem>> =
		laneDao.getAlleyLanes(alleyId)

	override fun getLanes(ids: List<UUID>): Flow<List<LaneListItem>> =
		laneDao.getLanes(ids)

	override fun getGameLanes(gameId: UUID): Flow<List<LaneListItem>> =
		laneDao.getGameLanes(gameId)

	override suspend fun insertLanes(lanes: List<LaneListItem>) {
		laneDao.insertAll(lanes.map(LaneListItem::asEntity))
	}

	override suspend fun setAlleyLanes(alleyId: UUID, lanes: List<LaneListItem>) =
		withContext(ioDispatcher) {
			transactionRunner {
				laneDao.deleteAlleyLanes(alleyId)
				laneDao.insertAll(lanes.map { it.asEntity(alleyId) })
			}
		}
}