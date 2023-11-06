package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.GearDao
import ca.josephroque.bowlingcompanion.core.database.model.asEntity
import ca.josephroque.bowlingcompanion.core.model.GearCreate
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import ca.josephroque.bowlingcompanion.core.model.GearUpdate
import ca.josephroque.bowlingcompanion.core.model.utils.sortByUUIDs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class OfflineFirstGearRepository @Inject constructor(
	private val gearDao: GearDao,
	private val userDataRepository: UserDataRepository,
): GearRepository {
	override fun getBowlerPreferredGear(bowlerId: UUID): Flow<List<GearListItem>> =
		gearDao.getBowlerPreferredGear(bowlerId)

	override fun getRecentlyUsedGear(limit: Int): Flow<List<GearListItem>> =
		combine(
			gearDao.getGearList(),
			userDataRepository.userData.map { it.recentlyUsedGearIds }
		) { gear, recentlyUsed ->
			gear.sortByUUIDs(recentlyUsed)
				.take(limit)
		}

	override fun getGearList(kind: GearKind?): Flow<List<GearListItem>> =
		gearDao.getGearList(kind)

	override suspend fun insertGear(gear: GearCreate) {
		gearDao.insertGear(gear.asEntity())
	}

	override suspend fun updateGear(gear: GearUpdate) {
		gearDao.updateGear(gear.asEntity())
	}

	override suspend fun deleteGear(id: UUID) {
		gearDao.deleteGear(id)
	}
}