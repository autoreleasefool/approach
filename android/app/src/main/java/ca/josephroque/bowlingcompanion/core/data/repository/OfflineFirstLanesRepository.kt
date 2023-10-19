package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.LaneDao
import ca.josephroque.bowlingcompanion.core.database.dao.TransactionRunner
import ca.josephroque.bowlingcompanion.core.database.model.LaneCreate
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class OfflineFirstLanesRepository @Inject constructor(
	private val laneDao: LaneDao,
	private val transactionRunner: TransactionRunner,
): LanesRepository {
	override fun alleyLanes(alleyId: UUID): Flow<List<LaneListItem>> =
		laneDao.getAlleyLanes(alleyId)

	override suspend fun overwriteAlleyLanes(alleyId: UUID, lanes: List<LaneCreate>) = transactionRunner {
		laneDao.deleteAlleyLanes(alleyId)
		laneDao.insertAll(lanes)
	}
}