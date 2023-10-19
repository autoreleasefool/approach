package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.LaneDao
import ca.josephroque.bowlingcompanion.core.database.model.LaneCreate
import ca.josephroque.bowlingcompanion.core.model.LaneListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class OfflineFirstLanesRepository @Inject constructor(
	private val laneDao: LaneDao,
): LanesRepository {
	override fun alleyLanes(alleyId: UUID): Flow<List<LaneListItem>> =
		laneDao.getAlleyLanes(alleyId)

	override suspend fun overwriteAlleyLanes(alleyId: UUID, lanes: List<LaneCreate>) {
		laneDao.overwriteAlleyLanes(alleyId, lanes)
	}
}