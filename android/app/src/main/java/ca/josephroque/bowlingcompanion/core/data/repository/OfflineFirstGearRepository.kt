package ca.josephroque.bowlingcompanion.core.data.repository

import ca.josephroque.bowlingcompanion.core.database.dao.GearDao
import ca.josephroque.bowlingcompanion.core.database.model.GearCreate
import ca.josephroque.bowlingcompanion.core.database.model.GearUpdate
import ca.josephroque.bowlingcompanion.core.model.GearListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class OfflineFirstGearRepository @Inject constructor(
	private val gearDao: GearDao,
): GearRepository {
	override fun getBowlerPreferredGear(bowlerId: UUID): Flow<List<GearListItem>> =
		gearDao.getBowlerPreferredGear(bowlerId)

	override suspend fun insertGear(gear: GearCreate) {
		gearDao.insertGear(gear)
	}

	override suspend fun updateGear(gear: GearUpdate) {
		gearDao.updateGear(gear)
	}

	override suspend fun deleteGear(id: UUID) {
		gearDao.deleteGear(id)
	}
}