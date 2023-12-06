package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.database.dao.LaneDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.LaneCreate
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
	override fun alleyLanes(alleyId: UUID): Flow<List<LaneListItem>> =
		laneDao.getAlleyLanes(alleyId)

	override suspend fun overwriteAlleyLanes(alleyId: UUID, lanes: List<LaneCreate>) =
		withContext(ioDispatcher) {
			transactionRunner {
				laneDao.deleteAlleyLanes(alleyId)
				laneDao.insertAll(lanes.map(LaneCreate::asEntity))
			}
		}
}